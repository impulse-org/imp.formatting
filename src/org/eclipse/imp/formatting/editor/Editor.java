package org.eclipse.imp.formatting.editor;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.formatting.spec.Parser;
import org.eclipse.imp.formatting.spec.Rule;
import org.eclipse.imp.formatting.spec.Specification;
import org.eclipse.imp.formatting.spec.Unparser;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.xml.sax.SAXException;

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

	protected Composite rules;

	protected Text example;

	private Specification spec;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			modified = true;
			firePropertyChange(PROP_DIRTY);
		}
	};
	
	public boolean modified = false;

	private Parser parser;

	private ScrolledComposite scroll;

	public Editor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		
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

	public void createStructuredEditor() {
		Composite parent = new Composite(getContainer(), SWT.NONE);
		parent.setLayout(new FillLayout());

		scroll = new ScrolledComposite(parent, SWT.BORDER
						| SWT.V_SCROLL);
		scroll.setExpandHorizontal(false);
		scroll.setExpandVertical(true);

		rules = new Composite(scroll, SWT.NONE);
		scroll.setContent(rules);

		int index = addPage(parent);
		setPageText(index, "Rules");
	}

	private void updateRuleEditor() {
		if (rules != null) {
		  rules.dispose();
		}
		
		rules = new Composite(scroll, SWT.NONE);
		scroll.setContent(rules);
		
		RowLayout rulesLayout = new RowLayout(SWT.VERTICAL);
		rulesLayout.spacing = 4;
		rulesLayout.fill = true;
		rulesLayout.wrap = false;
		rulesLayout.pack = true;
		rules.setLayout(rulesLayout);

		if (spec != null) {
			Iterator<Rule> iter = spec.ruleIterator();

			while (iter.hasNext()) {
				final Rule rule = iter.next();
				final Text t = new Text(rules, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
				t.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						String boxString = t.getText();
						try {
							parser.parseBoxAndObject(boxString, rule);
							modified = true;
							firePropertyChange(PROP_DIRTY);
						} catch (SAXException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				RowData data = new RowData();
				data.height = t.getLineHeight() * 5;
				data.width = 700;
				t.setLayoutData(data);
				t.setText(rule.getBoxString());
			}
			

			rules.setSize(rules.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			scroll.setMinSize(rules.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
	}
	
	public void createExampleViewer() {
		Composite parent = new Composite(getContainer(), SWT.NONE);
		parent.setLayout(new FillLayout());
		example = new Text(parent, SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		
		example.addModifyListener(new ModifyListener() {
			/* we parse the example, and when it is syntactically
			 * correct, the model is updated.
			 */
			public void modifyText(ModifyEvent e) {
				String objectString = example.getText();
				Object ast = parser.parseObject(objectString);
				if (ast != null) {
					System.err.println("example parsed ok");
					spec.setExample(objectString);
					spec.setExampleAst(ast);
					modified = true;
					firePropertyChange(PROP_DIRTY);
				}
			}
		});
		
		example.setToolTipText("Example source code");
		
		int index = addPage(parent);
		setPageText(index, "Example");
	}

	protected void updateExample() {
		if (spec != null) {
			  example.setText(spec.getExample());
		}
	}
	
	protected void updateStructuredEditor() {
		
	}
	
	protected void createPages() {
		createPlainEditor();
		createStructuredEditor();
		createExampleViewer();
		
		updateModelFromFile();
		updateRuleEditor();
		updateExample();
		
		modified = false;
	}

	private void updateModelFromFile() {
		try {
			parser = new Parser(((IFileEditorInput) getEditorInput()).getFile());
			spec = null;
			String editorText = editor.getDocumentProvider().getDocument(
					editor.getEditorInput()).get();
			spec = parser.parse(editorText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public void doSave(IProgressMonitor monitor) {
		System.err.println("saving!");
		if (editor.isDirty()) {
			editor.doSave(monitor);
			updateModelFromFile();
			updateRuleEditor();
			updateExample();
		}
		else if (modified) {
			Unparser u = new Unparser();
			String newText = u.unparse(spec);
			
			System.err.println("unparsed text:" + newText);
			
			// TODO: this is probably to slow for large
			// specifications. Other editors like the plugin.xml
			// editor constantly update parts of the underlying
			// xml file. This comes with a lot of complexity though.
			editor.getDocumentProvider().getDocument(
					editor.getEditorInput()).set(newText);
			editor.doSave(monitor);
			updateModelFromFile();
			updateRuleEditor();
			updateExample();
		}
		
		modified = false;
		firePropertyChange(PROP_DIRTY);
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
		return editor.isDirty() || modified;
	}
}