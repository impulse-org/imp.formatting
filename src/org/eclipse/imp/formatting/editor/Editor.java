package org.eclipse.imp.formatting.editor;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.box.builders.BoxFactory;
import org.eclipse.imp.formatting.spec.ExtensionPointBinder;
import org.eclipse.imp.formatting.spec.ParseException;
import org.eclipse.imp.formatting.spec.Parser;
import org.eclipse.imp.formatting.spec.Rule;
import org.eclipse.imp.formatting.spec.Specification;
import org.eclipse.imp.formatting.spec.Transformer;
import org.eclipse.imp.formatting.spec.Unparser;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.xform.pattern.matching.IASTAdapter;
import org.eclipse.jface.action.AbstractAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * This is the start of a prototype generic formatting tool for IMP.
 * 
 * This class is the main editor view for formatting specifications. A
 * formatting specification will be stored in an XML file containing the name of
 * the language that is to be formatted, a list of formatting rules and an
 * example program.
 * 
 * The rules are now Box expressions, out of which the source code is to be
 * extracted and parsed to form patterns. The Box expressions are to be parsed
 * too. A formatting rule defines the mapping from source code to Box.
 * 
 * The Box rules are continuously applied to the example source code such that
 * the user can see the effect of the specification.
 */

public class Editor extends MultiPageEditorPart implements
		IResourceChangeListener {
	protected TextEditor editor;

	protected ScrolledComposite scroll;

	protected Text example;

	private Specification model;

	private boolean rulesModified = false;

	private boolean exampleModified = false;

	private Parser parser;
	
	private Rule activeRule;

	public Editor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		LanguageRegistry.getLanguages();
	}

	public Specification getModel() {
		return model;
	}

	void createPlainEditor() {
		try {
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, "Plain text");
			setPartName(editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	
	public void createRuleEditor() {
		Composite parent = new Composite(getContainer(), SWT.NONE);
		parent.setLayout(new FillLayout());

		scroll = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL);
		scroll.setExpandHorizontal(false);
		scroll.setExpandVertical(true);

		int index = addPage(parent);
		setPageText(index, "Rules");
		
		createRuleActions();
	}

	

	private void createRuleActions() {
		editor.setAction("newRule", new Action() {
			public void run() {
				newRule();
			}
		});
		
		editor.setAction("deleteRule", new Action() {
			public void run() {
				deleteRule();
			}
		});
	}

	private void updateRuleEditor() {
		Composite rules = new Composite(scroll, SWT.NONE);
		scroll.setContent(rules);

		RowLayout rulesLayout = new RowLayout(SWT.VERTICAL);
		rulesLayout.spacing = 4;
		rulesLayout.fill = true;
		rulesLayout.wrap = false;
		rulesLayout.pack = true;
		rules.setLayout(rulesLayout);

		if (model != null) {
			Iterator<Rule> iter = model.ruleIterator();

			while (iter.hasNext()) {
				final Rule rule = iter.next();
				final Text t = new Text(rules, SWT.BORDER | SWT.V_SCROLL
						| SWT.WRAP);
				RowData data = new RowData();
				data.height = t.getLineHeight() * 5;
				data.width = 700;
				t.setLayoutData(data);
				t.setText(rule.getBoxString());
				t.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						rule.setBoxString(t.getText());
						rulesModified = true;
						firePropertyChange(PROP_DIRTY);
					}
				});
				t.setData("rule", rule);
				t.addFocusListener(new FocusListener() {
					public void focusGained(FocusEvent e) {
						activeRule = (Rule) ((Text) e.getSource()).getData("rule");
					}

					public void focusLost(FocusEvent e) {
						activeRule = null;
					}
				});
			}

			rules.setSize(rules.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			scroll.setMinSize(rules.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}

		rulesModified = false;
		firePropertyChange(PROP_DIRTY);
	}

	public void createExampleViewer() {
		Composite parent = new Composite(getContainer(), SWT.NONE);
		parent.setLayout(new FillLayout());
		example = new Text(parent, SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);

		example.setFont(new Font(example.getDisplay(),"Monospace",10,0));
		
		example.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				model.setExample(example.getText());
				exampleModified = true;
				firePropertyChange(PROP_DIRTY);
			}
		});

		example.setToolTipText("Example source code");

		int index = addPage(parent);
		setPageText(index, "Example");
	}

	protected void updateExample() {
		if (model != null) {
			reformatExample();
		}
	}

	protected void createPages() {
		createPlainEditor();
		createRuleEditor();
		createExampleViewer();
		

		updateModelFromFile();
		updateRuleEditor();
		updateExample();
		

		rulesModified = false;
		exampleModified = false;
	}

	private void updateModelFromFile() {
		try {
			// TODO bind extension points for this editor too
			IPath path = ((IFileEditorInput) getEditorInput()).getFile()
					.getProjectRelativePath();

			// TODO: ooof, what a casting
			IProject project = ((IFileEditorInput) getEditorInput()).getFile()
					.getProject();
			IPath fullFilePath = project.getLocation().append(path);
			ISourceProject sp = ModelFactory.open(project);

			parser = new Parser(fullFilePath, sp);
			String editorText = editor.getDocumentProvider().getDocument(
					editor.getEditorInput()).get();
			model = parser.parse(editorText);

		} catch (ParseException e) {
			System.err.println("error:" + e);
		} catch (ModelFactory.ModelException e) {
			System.err.println("model error:" + e);
		}
	}

	private void reformatExample() {
		Object ast = model.getExampleAst();

		if (ast != null) {

			Language objectLanguage = LanguageRegistry.findLanguage(model
					.getLanguage());
			ExtensionPointBinder b = new ExtensionPointBinder(objectLanguage);
			Transformer t = new Transformer(model, b.getASTAdapter());
			String box = t.transformToBox(model.getExample(), model
					.getExampleAst());
			String newExample = null;

			try {
				newExample = BoxFactory.fastbox2text(box);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			example.setText(newExample);
			exampleModified = false;
		}
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public void doSave(IProgressMonitor monitor) {
		if (rulesModified || exampleModified) {
			Unparser u = new Unparser();
			String newText = u.unparse(model);

			editor.getDocumentProvider().getDocument(editor.getEditorInput())
					.set(newText);
			editor.doSave(monitor);
			updateModelFromFile();
			updateExample();
			updateRuleEditor();
		} else if (editor.isDirty()) {
			editor.doSave(monitor);
			updateModelFromFile();
			updateExample();
			updateRuleEditor();
		}
	}

	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) editor.getEditorInput())
								.getFile().getProject().equals(
										event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(editor
									.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	public boolean isDirty() {
		return editor.isDirty() || rulesModified || exampleModified;
	}

	public void newRule() {
		model.addRule(new Rule());
		rulesModified = true;
		updateRuleEditor();
	}
	
	public void deleteRule() {
		if (activeRule != null) {
		  model.removeRule(activeRule);
		}
	}
}