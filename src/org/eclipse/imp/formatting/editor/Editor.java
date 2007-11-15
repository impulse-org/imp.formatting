package org.eclipse.imp.formatting.editor;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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
import org.eclipse.ui.texteditor.IDocumentProvider;

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
	private static final int PlainEditorIndex = 0; // must be 0

	private static final int RuleEditorIndex = 1;

	private static final int ExampleEditorIndex = 2;

	protected TextEditor editor;

	protected Text example;

	private Specification model;

	private boolean rulesModified = false;

	private boolean exampleModified = false;

	private Parser parser;

	private Rule activeRule;

	private Table ruleTable;

	private TableEditor tableEditor;

	private final int STATUS_COLUMN = 0;

	private final int EDIT_COLUMN = 1;

	private final int PREVIEW_COLUMN = 2;

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
			editor = new TextEditor() {
				@Override
				protected void setDocumentProvider(IDocumentProvider provider) {
					System.err
							.println("somebodies overwriting documentprovider... to:"
									+ provider);
					super.setDocumentProvider(provider);
				}
			};
			addPage(PlainEditorIndex, editor, getEditorInput());
			setPageText(PlainEditorIndex, "Plain text");
			setPartName(editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
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

	private void createRuleEditor(Specification model) {
		Composite parent = new Composite(getContainer(), SWT.NONE);
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		ruleTable = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION);
		ruleTable.setLinesVisible(true);
		ruleTable.setHeaderVisible(true);

		TableColumn status = new TableColumn(ruleTable, SWT.NONE);
		status.setText("Status");
		status.setResizable(true);

		TableColumn box = new TableColumn(ruleTable, SWT.NONE);
		box.setText("Box");
		box.setResizable(true);

		TableColumn preview = new TableColumn(ruleTable, SWT.NONE);
		preview.setText("Preview");
		preview.setResizable(true);

		Iterator<Rule> iter = model.ruleIterator();

		while (iter.hasNext()) {
			final Rule rule = iter.next();
			TableItem item = new TableItem(ruleTable, SWT.NONE);
			initRuleTableItem(rule, item);
		}

		status.pack();
		box.pack();
		preview.pack();

		tableEditor = new TableEditor(ruleTable);
		tableEditor.horizontalAlignment = SWT.LEFT;
		tableEditor.verticalAlignment = SWT.TOP;
		tableEditor.grabHorizontal = true;
		tableEditor.grabVertical = true;
		tableEditor.minimumWidth = 50;

		ruleTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Control oldEditor = tableEditor.getEditor();
				if (oldEditor != null) {
					oldEditor.dispose();
				}

				TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				activeRule = (Rule) item.getData();

				final Text newEditor = new Text(ruleTable, SWT.MULTI | SWT.WRAP
						| SWT.BORDER);
				newEditor.setText(item.getText(EDIT_COLUMN));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						Text text = (Text) tableEditor.getEditor();
						String b = text.getText();
						TableItem i = tableEditor.getItem();
						i.setText(EDIT_COLUMN, b);
						Rule rule = (Rule) tableEditor.getItem().getData();

						rule.setBoxString(b);
						updateRuleTableItem(i, b);

						if (!rulesModified) {
							rulesModified = true;
							firePropertyChange(PROP_DIRTY);
						}

					}
				});

				newEditor.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						newEditor.dispose();
					}
				});

				newEditor.setFocus();
				tableEditor.setEditor(newEditor, item, EDIT_COLUMN);
			}

		});

		addPage(RuleEditorIndex, parent);
		setPageText(RuleEditorIndex, "Rules");

		createRuleActions();
	}

	private void initRuleTableItem(final Rule rule, TableItem item) {
		item.setData(rule);
		updateRuleTableItem(item, rule.getBoxString());
	}

	private void updateRuleTableItem(TableItem item, String boxString) {
		item.setText(EDIT_COLUMN, boxString == null ? "\n" : boxString);

		if (boxString != null) {
			if (parser.parseBox(boxString) != null) {
				String formatted = getFormattedBox(boxString);
				item.setText(PREVIEW_COLUMN, formatted);

				if (parser.parseObject(formatted) == null) {
					item.setText(STATUS_COLUMN, "error in preview");
				} else {
					item.setText(STATUS_COLUMN, "ok");
				}
			}
			else {
			   item.setText(STATUS_COLUMN, "error in box");
			}
		} else {
			item.setText(STATUS_COLUMN, "no box");
		}
	}

	protected String getFormattedBox(String boxString) {
		try {
			if (boxString != null && boxString.length() > 0) {
				return BoxFactory.box2text(boxString);
			} else {
				return "";
			}
		} catch (IOException e) {
			return "";
		} catch (InterruptedException e) {
			return "";
		}
	}

	public void createExampleViewer() {
		Composite parent = new Composite(getContainer(), SWT.NONE);
		parent.setLayout(new FillLayout());
		example = new Text(parent, SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);

		example.setFont(new Font(example.getDisplay(), "Monospace", 10, 0));

		example.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				model.setExample(example.getText());
				exampleModified = true;
				firePropertyChange(PROP_DIRTY);

				Object ast = parser.parseObject(example.getText());
				if (ast != null) {
					model.setExampleAst(ast);
				}
			}
		});

		example.setToolTipText("Example source code");

		addPage(ExampleEditorIndex, parent);
		setPageText(ExampleEditorIndex, "Example");
	}

	protected void updateExample() {
		if (model != null) {
			reformatExample();
		}
	}

	protected void createPages() {
		createPlainEditor();

		model = updateModelFromFile();
		createRuleEditor(model);
		createExampleViewer();

		updateExample();

		rulesModified = false;
		exampleModified = false;
	}

	private Specification updateModelFromFile() {
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
			return model;
		} catch (ParseException e) {
			System.err.println("error:" + e);
		} catch (ModelFactory.ModelException e) {
			System.err.println("model error:" + e);
		}

		return new Specification();
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
				newExample = BoxFactory.box2text(box);
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
			updateExample();
			rulesModified = false;
			exampleModified = false;
			firePropertyChange(PROP_DIRTY);
		} else if (editor.isDirty()) {
			editor.doSave(monitor);
			updateModelFromFile();
			updateExample();
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
		Rule r = new Rule();

		if (activeRule != null) {
			disposeTableEditor();
			int i = model.getRules().indexOf(activeRule);
			model.addRule(i, r);
			TableItem item = new TableItem(ruleTable, SWT.NONE, i);
			initRuleTableItem(r, item);
			ruleTable.select(i);
		} else {
			model.addRule(r);
			TableItem item = new TableItem(ruleTable, SWT.NONE);
			initRuleTableItem(r, item);
			ruleTable.select(ruleTable.getChildren().length);
		}

		setRulesModified(true);
	}

	private void disposeTableEditor() {
		Control e = tableEditor.getEditor();
		if (e != null) {
			e.dispose();
		}
	}

	private void setRulesModified(boolean value) {
		rulesModified = true;
		firePropertyChange(PROP_DIRTY);
	}

	public void deleteRule() {
		if (activeRule != null) {
			disposeTableEditor();
			int i = model.getRules().indexOf(activeRule);
			model.removeRule(i);
			ruleTable.remove(i);
			ruleTable.deselectAll();
			setRulesModified(true);
		}
	}

	public void formatRule() {
		if (activeRule != null) {
			disposeTableEditor();
			String box = activeRule.getBoxString();
			String formatted;
			try {
				formatted = BoxFactory.formatBox(box);
				if (formatted != null) {
					activeRule.setBoxString(formatted);
					int i = model.getRules().indexOf(activeRule);
					ruleTable.getItem(i).setText(formatted);
					setRulesModified(true);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}