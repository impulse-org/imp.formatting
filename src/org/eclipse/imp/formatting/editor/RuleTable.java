package org.eclipse.imp.formatting.editor;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.box.builders.BoxException;
import org.eclipse.imp.box.builders.BoxFactory;
import org.eclipse.imp.formatting.spec.BoxStringBuilder;
import org.eclipse.imp.formatting.spec.Item;
import org.eclipse.imp.formatting.spec.Parser;
import org.eclipse.imp.formatting.spec.Rule;
import org.eclipse.imp.formatting.spec.Separator;
import org.eclipse.imp.formatting.spec.SpaceOptionBinder;
import org.eclipse.imp.formatting.spec.Specification;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

public class RuleTable implements IEditorPart {
	private Table ruleTable;
	private TableEditor tableEditor;

	private final int STATUS_COLUMN = 0;
	private final int EDIT_COLUMN = 1;
	private final int PREVIEW_COLUMN = 2;
	
	private final int MARGIN = 2;
	
	private Item activeItem;
	
	private List<IPropertyListener> listeners;
	
	private Specification model;
	private boolean dirty = false;
	private IEditorSite site;
	private IEditorInput input;
	
	public RuleTable() {
		listeners = new LinkedList<IPropertyListener>();
	}
	
	public void setModel(Specification model) {
		this.model = model;
		refresh();
	}
	
    public void addPropertyListener(IPropertyListener l) {
        listeners.add(l);
    }
    
    private void firePropertyChange(int change) {
    	for (IPropertyListener l : listeners) {
    		l.propertyChanged(this, change);
    	}
    }
    
    public IEditorInput getEditorInput() {
		return input;
	}

	public IEditorSite getEditorSite() {
		return site;
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.site = site;
		this.input = input;
	}

	public void dispose() {
		ruleTable.dispose();
	}

	public IWorkbenchPartSite getSite() {
		return site;
	}

	public String getTitle() {
		return "Rules";
	}

	public Image getTitleImage() {
		return null;
	}

	public String getTitleToolTip() {
		return null;
	}

	
	public void removePropertyListener(IPropertyListener listener) {
		listeners.remove(listener);
	}

	public void setFocus() {
		ruleTable.setFocus();
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void doSave(IProgressMonitor monitor) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void doSaveAs() {
		throw new UnsupportedOperationException("not implemented");
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		return false;
	}
    
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		ruleTable = new Table(parent, SWT.FULL_SELECTION);
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

		status.pack();
		box.pack();
		preview.pack();

		createCellEditor();
		createCellPainter();
		createCellTooltip();
	}
	
	private void createCellEditor() {
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

				activeItem = (Item) item.getData();

				final Text newEditor = new Text(ruleTable, SWT.MULTI | SWT.WRAP
						| SWT.BORDER);
				newEditor.setFont(item.getFont());
				newEditor.setText(item.getText(EDIT_COLUMN));

				if (activeItem instanceof Rule) {
					newEditor.addModifyListener(new RuleModifier());
				} else if (activeItem instanceof Separator) {
					newEditor.addModifyListener(new SeparatorModifier());
				}

				newEditor.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						newEditor.dispose();
					}
				});

				newEditor.setFocus();
				tableEditor.setEditor(newEditor, item, EDIT_COLUMN);
			}

		});
	}
	
	private void disposeTableEditor() {
		Control e = tableEditor.getEditor();
		if (e != null) {
			e.dispose();
		}
	}

	private void createCellTooltip() {
		ruleTable.setToolTipText("");

		Listener tooltipListener = new Listener() {
			Shell tip = null;

			Label label = null;

			Display display = ruleTable.getDisplay();

			Shell shell = ruleTable.getShell();

			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Dispose:
				case SWT.KeyDown:
				case SWT.MouseMove: {
					if (tip == null)
						break;
					tip.dispose();
					tip = null;
					label = null;
					break;
				}
				case SWT.MouseHover: {
					TableItem item = ruleTable.getItem(new Point(event.x,
							event.y));
					if (item != null) {
						if (tip != null && !tip.isDisposed()) {
							tip.dispose();
						}
						tip = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
						tip.setLayout(new FillLayout());
						label = new Label(tip, SWT.NONE);
						label.setForeground(display
								.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
						label.setBackground(ruleTable.getDisplay()
								.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
						String text = (String) item.getData("tooltip");
						if (text != null) {
							label.setText(text);

							Point size = tip.computeSize(SWT.DEFAULT,
									SWT.DEFAULT);
							Rectangle rect = item.getBounds(0);
							Point pt = ruleTable.toDisplay(rect.x, rect.y);
							tip.setBounds(pt.x + size.y, pt.y - size.y, size.x,
									size.y);
							tip.setVisible(true);
						}
					}
				}
				}
			}
		};

		ruleTable.addListener(SWT.KeyDown, tooltipListener);
		ruleTable.addListener(SWT.Dispose, tooltipListener);
		ruleTable.addListener(SWT.MouseHover, tooltipListener);
		ruleTable.addListener(SWT.MouseMove, tooltipListener);
	}

	private void createCellPainter() {
		ruleTable.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				event.gc.setFont(item.getFont());
				Point size = event.gc.textExtent(text, SWT.DRAW_DELIMITER);
				event.width = size.x + 2 * MARGIN;
				event.height = size.y + MARGIN;
			}
		});
		ruleTable.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				event.detail &= ~SWT.FOREGROUND;
			}
		});
		ruleTable.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				event.gc.setFont(item.getFont());
				event.gc.drawText(text, event.x + MARGIN, event.y + MARGIN, true);
			}
		});
	}

	public void addSeparator() {
		Separator s = new Separator();

		if (activeItem != null) {
			disposeTableEditor();
			int i = model.getRules().indexOf(activeItem);
			model.addSeparator(i, s);
			TableItem item = new TableItem(ruleTable, SWT.NONE, i);
			initSeparatorTableItem(s, item);
			ruleTable.select(i);
			activeItem = s;
		} else {
			model.addRule(s);
			TableItem item = new TableItem(ruleTable, SWT.NONE);
			initSeparatorTableItem(s, item);
			ruleTable.select(ruleTable.getChildren().length);
		}
	}

	private void initSeparatorTableItem(Separator s, TableItem item) {
		item.setData(s);
		updateSeparatorTableItem(item, s.getLabel());
	}

	private void updateSeparatorTableItem(TableItem item, String label) {
		item.setText(EDIT_COLUMN, label);
		item.setText(PREVIEW_COLUMN, "");
		item.setText(STATUS_COLUMN, "---");
		Font font = new Font(ruleTable.getDisplay(), "Monospace", 14, SWT.BOLD);
		item.setFont(font);
	}

	private final class RuleModifier implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			Text text = (Text) tableEditor.getEditor();
			String b = text.getText();
			TableItem i = tableEditor.getItem();
			i.setText(EDIT_COLUMN, b);
			Rule rule = (Rule) tableEditor.getItem().getData();

			rule.setBoxString(b);
			updateRuleTableItem(i, b);
			setDirty(true);
		}
	}

	private final class SeparatorModifier implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			Text text = (Text) tableEditor.getEditor();
			String l = text.getText();
			TableItem i = tableEditor.getItem();
			i.setText(EDIT_COLUMN, l);
			Separator sep = (Separator) tableEditor.getItem().getData();

			sep.setLabel(l);
			updateSeparatorTableItem(i, l);
			setDirty(true);
		}
	}
	
	public void refresh() {
		ruleTable.removeAll();

		Iterator<Item> iter = model.ruleIterator();

		while (iter.hasNext()) {
			final Item i = iter.next();
			TableItem item = new TableItem(ruleTable, SWT.NONE);

			if (i instanceof Rule) {
				initRuleTableItem((Rule) i, item);
			} else if (i instanceof Separator) {
				initSeparatorTableItem((Separator) i, item);
			}
		}

		for (TableColumn c : ruleTable.getColumns()) {
			c.pack();
		}
	}
	
	public void setDirty(boolean b) {
		if (dirty != b) {
		  dirty = b;
		  firePropertyChange(PROP_DIRTY);
		}
	}

	private void initRuleTableItem(final Rule rule, TableItem item) {
		item.setData(rule);
		updateRuleTableItem(item, rule.getBoxString());
	}

	private void updateRuleTableItem(TableItem item, String boxString) {
		item.setText(EDIT_COLUMN, boxString == null ? "\n" : boxString);

		if (boxString != null) {
			Parser parser = model.getParser();
			
			if (parser.parseBox(boxString) != null) {
				String formatted = getFormattedBox(boxString);
				item.setText(PREVIEW_COLUMN, formatted);
				Object ast = parser.parseObject(formatted);

				if (ast == null) {
					item.setText(STATUS_COLUMN, "error in preview");
					item.setData("tooltip", "");
				} else {
					Rule rule = (Rule) item.getData();
					rule.setPatternAst(ast);
					item.setText(STATUS_COLUMN, "ok");
					item.setData("tooltip", ast.getClass().getName());
				}
			} else {
				item.setText(STATUS_COLUMN, "error in box");
			}
		} else {
			item.setText(STATUS_COLUMN, "no box");
		}
	}

	private String getFormattedBox(String boxString) {
		try {
			if (boxString != null && boxString.length() > 0) {
				SpaceOptionBinder binder = new SpaceOptionBinder(model);
				boxString = binder.bind(boxString);
				return BoxFactory.box2text(boxString);
			} else {
				return "";
			}
		} catch (BoxException e) {
			return "";
		}
	}
	
	public void move(int diff) {
		if (activeItem != null && activeItem instanceof Rule) {
			Rule r = (Rule) activeItem;
			List rules = model.getRules();
			int cur = rules.indexOf(activeItem);
			
			if (cur + diff >= 0) {
				disposeTableEditor();
				
				ruleTable.remove(cur);
				model.removeRule(activeItem);
				model.addRule(cur + diff, r);
				
				TableItem item = new TableItem(ruleTable, SWT.NONE, cur + diff);
				initRuleTableItem(r, item);
				ruleTable.select(cur + diff);
				
				setDirty(true);
			}
		}
	}
	
	public void deleteRule() {
		if (activeItem != null) {
			disposeTableEditor();
			int i = model.getRules().indexOf(activeItem);
			ruleTable.deselectAll();
			ruleTable.remove(i);
			model.removeRule(i);
			setDirty(true);
		}
	}
	
	public void newRule() {
		Rule r = new Rule();

		if (activeItem != null) {
			disposeTableEditor();
			int i = model.getRules().indexOf(activeItem);
			model.addRule(i, r);
			TableItem item = new TableItem(ruleTable, SWT.NONE, i);
			initRuleTableItem(r, item);
			ruleTable.select(i);
			activeItem = r;
			setDirty(true);
		} else {
			model.addRule(r);
			TableItem item = new TableItem(ruleTable, SWT.NONE);
			initRuleTableItem(r, item);
			ruleTable.select(ruleTable.getChildren().length);
			setDirty(true);
		}
	}
	
	public void formatRule() {
		if (activeItem != null && activeItem instanceof Rule) {
			Rule rule = (Rule) activeItem;
			disposeTableEditor();
			String box = rule.getBoxString();
			String formatted;
			try {
				formatted = BoxFactory.formatBox(box);
				if (formatted != null) {
					rule.setBoxString(formatted);
					int i = model.getRules().indexOf(rule);
					ruleTable.getItem(i).setText(EDIT_COLUMN, formatted);
					setDirty(true);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addRuleFromExample() {
		disposeTableEditor();

		IInputValidator v = new IInputValidator() {
			public String isValid(String newText) {
				Parser parser = model.getParser();
				if (parser.parseObject(newText) != null) {
					return null;
				} else {
					return "Not a valid string";
				}
			}
		};

		InputDialog dialog = new InputDialog(ruleTable.getShell(),
				"provide your example", "", null, v);

		dialog.setBlockOnOpen(true);
		dialog.open();

		if (dialog.getReturnCode() == Window.OK) {
			String result = dialog.getValue();
			if (result != null) {
				newRule();
				Rule rule = (Rule) activeItem;
				String box = BoxStringBuilder.exampleToBox(result);
				rule.setBoxString(box);
				int i = model.getRules().indexOf(activeItem);
				TableItem item = ruleTable.getItem(i);
				updateRuleTableItem(item, box);
				setDirty(true);
			}
		}
	}

	public boolean isDirty() {
		return dirty;
	}
}
