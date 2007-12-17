package org.eclipse.imp.formatting.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.box.builders.BoxFactory;
import org.eclipse.imp.formatting.spec.ExtensionPointBinder;
import org.eclipse.imp.formatting.spec.ParseException;
import org.eclipse.imp.formatting.spec.Parser;
import org.eclipse.imp.formatting.spec.Specification;
import org.eclipse.imp.formatting.spec.Transformer;
import org.eclipse.imp.formatting.spec.Unparser;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
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

	private static final int RuleEditorIndex = 0;

	private static final int ExampleEditorIndex = 1;

	private static final int OptionEditorIndex = 2;
	
	protected TextEditor editor;

	protected Text example;

	private Specification model;

	private boolean exampleModified = false;

	private Parser parser;

	private RuleTable ruleTable;

	private SpaceOptionTable spaceTable;

	private ModifyListener exampleModifier;

	public Editor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		LanguageRegistry.getLanguages();
	}

	public Specification getModel() {
		return model;
	}

	public void createExampleViewer() {
		Composite parent = new Composite(getContainer(), SWT.NONE);
		parent.setLayout(new FillLayout());
		example = new Text(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);

		example.setFont(new Font(example.getDisplay(), "Monospace", 10, 0));

		exampleModifier = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				model.setExample(example.getText());
				exampleModified = true;
				firePropertyChange(PROP_DIRTY);

				Object ast = parser.parseObject(example.getText());
				if (ast != null) {
					model.setExampleAst(ast);
				}
			}
		};
		example.addModifyListener(exampleModifier);
		
		addPage(ExampleEditorIndex, parent);
		setPageText(ExampleEditorIndex, "Example");
	}

	protected void updateExample() {
		if (model != null) {
			String current = example.getText();

			if (current == null || current.length() == 0) {
				example.setText(model.getExample());
			}
			reformatExample();
		}
	}

	protected void createPages() {
		createRuleEditor();
		IEditorInput input = ruleTable.getEditorInput();
		
		setPartName(input.getName());
		
		model = updateModelFromFile(input);
			
		createExampleViewer();
		createOptionEditor();
		
		ruleTable.setModel(model);
		spaceTable.setModel(model);
		updateExample();
		
		exampleModified = false;
	}

	private void createOptionEditor() {
		spaceTable = new SpaceOptionTable(model);
		spaceTable.addPropertyListener(new IPropertyListener() {
			public void propertyChanged(Object source, int propId) {
				firePropertyChange(PROP_DIRTY);
			}
		});
		
		try {
			addPage(OptionEditorIndex, spaceTable, getEditorInput());
			setPageText(OptionEditorIndex, spaceTable.getTitle());
			
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createRuleEditor() {
		ruleTable = new RuleTable();
		
		ruleTable.addPropertyListener(new IPropertyListener() {
			public void propertyChanged(Object source, int propId) {
				firePropertyChange(PROP_DIRTY);
			}
		});
		
		try {
			addPage(RuleEditorIndex, ruleTable, getEditorInput());
			setPageText(RuleEditorIndex, ruleTable.getTitle());
			
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Specification updateModelFromFile(IEditorInput input) {
		try {
			// TODO bind extension points for this editor too
			IPath path = ((IFileEditorInput) getEditorInput()).getFile()
					.getProjectRelativePath();

			// TODO: ooof, what a casting
			IProject project = ((IFileEditorInput) input).getFile()
					.getProject();
			IPath fullFilePath = project.getLocation().append(path);
			ISourceProject sp = ModelFactory.open(project);

			parser = new Parser(fullFilePath, sp, new IMessageHandler() {
			    public void startMessageGroup(String groupName) { }
			    public void endMessageGroup() { }

			    public void handleSimpleMessage(String msg, int startOffset, int endOffset, int startCol, int endCol, int startLine, int endLine) {
			        System.err.println("parse error:");
			        System.err.println("\tline: " + startLine);
			        System.err.println("\tcolumn: " + startCol);
			    }
			});

			IFile file = ((IFileEditorInput) input).getFile();
			String editorText = StreamUtils.readStreamContents(file.getContents());
			model = parser.load(editorText);
			return model;
		} catch (ParseException e) {
			System.err.println("error:" + e);
		} catch (ModelFactory.ModelException e) {
			System.err.println("model error:" + e);
		} catch (CoreException e) {
			System.err.println("file reading error:" + e);
		}

		return new Specification(parser);
	}

	private void reformatExample() {
		Object ast = parser.parseObject(model.getExample());

		if (ast != null) {
			model.setExampleAst(ast);

			try {
				Language objectLanguage = LanguageRegistry.findLanguage(model
						.getLanguage());
				ExtensionPointBinder b = new ExtensionPointBinder(
						objectLanguage);
				Transformer t = new Transformer(model, b.getASTAdapter());
				String box = t.transformToBox(model.getExample(), model
						.getExampleAst());
				String newExample;

				newExample = BoxFactory.box2text(box);

				example.removeModifyListener(exampleModifier);
				example.setText(newExample);
				example.addModifyListener(exampleModifier);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: something useful
				e.printStackTrace();
			}
		}
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			Unparser u = new Unparser();
			String contents = u.unparse(model);
			
			IFile file = ((FileEditorInput) getEditorInput()).getFile();
			InputStream s = new ByteArrayInputStream(contents.getBytes());
			file.setContents(s, 0, monitor);
			
			ruleTable.setDirty(false);
			spaceTable.setDirty(false);
			exampleModified = false;

			firePropertyChange(PROP_DIRTY);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void doSaveAs() {
		// not allowed
	}
	
	public boolean isSaveAsAllowed() {
		return false;
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

	protected void pageChange(int newPageIndex) {
		switch (newPageIndex) {
		case OptionEditorIndex:
			spaceTable.refresh();
			break;
		case ExampleEditorIndex:
			updateExample();
			break;
		case RuleEditorIndex:
			// ruleTable.refresh(); too 
//		case PlainEditorIndex:
		}
		
		super.pageChange(newPageIndex);
	}
	
	public boolean isDirty() {
		return ruleTable.isDirty() || spaceTable.isDirty() || exampleModified;
	}

	public void newRule() {
		ruleTable.newRule();
	}
	
	public void addSeparator() {
		ruleTable.addSeparator();
	}

	public void deleteRule() {
	   ruleTable.deleteRule();
	}

	public void formatRule() {
		ruleTable.formatRule();
	}

	public void addRuleFromExample() {
		ruleTable.addRuleFromExample();
	}

	public void moveUp() {
		ruleTable.move(-1);
	}
	
	public void moveDown() {
		ruleTable.move(1);
	}

	public void addOption() {
		spaceTable.newOption();
	}

	public void deleteOption() {
		spaceTable.deleteOption();
	}
	
}