package org.eclipse.imp.formatting.views;

import java.io.File;
import java.util.Iterator;

import org.eclipse.imp.formatting.spec.FormattingRule;
import org.eclipse.imp.formatting.spec.FormattingSpecification;
import org.eclipse.imp.formatting.spec.FormattingSpecificationParser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * This is the start of a prototype generic formatting tool for IMP.
 * 
 * This class is the main editor view for formatting specifications. A formatting
 * specification will be stored in an XML file containing the name of the language that
 * is to be formatted, a list of formatting rules and an example program. 
 * 
 * The rules are now Box expressions, out of which the source code is to be extracted and
 * parsed to form patterns. The Box expressions are to be parsed too. A formatting rule
 * defines the mapping from source code to Box. 
 * 
 * The Box rules are continuously applied to the example source code such that the user
 * can see the effect of the specification.
 */

public class FormattingRulesView extends ViewPart {

	protected Composite rules;

	protected Text example;

	public void setFocus() {
		rules.setFocus();
	}
	
	

	public void createPartControl(Composite parent) {
		FormattingSpecificationParser p = new FormattingSpecificationParser();
		FormattingSpecification spec;
		
		try {
			 spec = p.parse(new File("test.fmt"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		SashForm pane = new SashForm(parent, SWT.NONE);
		FillLayout parentLayout = new FillLayout(SWT.VERTICAL);
		parent.setLayout(parentLayout);

		ScrolledComposite scroll = new ScrolledComposite(pane, SWT.BORDER | SWT.V_SCROLL);
		scroll.setExpandHorizontal(false);
		scroll.setExpandVertical(true);
		
		rules = new Composite(scroll, SWT.NONE);
		scroll.setContent(rules);
		
		RowLayout rulesLayout = new RowLayout(SWT.VERTICAL);
		rulesLayout.spacing = 4;
		rulesLayout.fill = true;
		rulesLayout.wrap = false;
		rules.setLayout(rulesLayout);

		Label rulesLabel = new Label(rules, SWT.BOLD | SWT.CENTER);
		rulesLabel.setText("Formatting rules");
		
		Iterator<FormattingRule> iter = spec.ruleIterator();
		
		while (iter.hasNext()) {
			FormattingRule rule = iter.next();
			Text t = new Text(rules, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
			RowData data = new RowData();
			data.height = t.getLineHeight() * 5;
			data.width = 400;
			t.setLayoutData(data);
			t.setText(rule.getBoxString());
		}
		
		rules.setSize(rules.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scroll.setMinSize(rules.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		example = new Text(pane, SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		example.setText(spec.getExample());
		example.setToolTipText("Example source code");
	}
}