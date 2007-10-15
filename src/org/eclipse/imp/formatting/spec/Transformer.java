package org.eclipse.imp.formatting.spec;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.imp.xform.pattern.matching.IASTAdapter;

public class Transformer {
	private Specification spec;

	private IASTAdapter adapter;
	
	private Matcher matcher;

	public Transformer(Specification spec, IASTAdapter adapter) {
		this.spec = spec;
		this.adapter = adapter;
		this.matcher = new Matcher(adapter);
	}

	public String transformToBox(Object ast) {
		Object[] kids = adapter.getChildren(ast);
		String kidsString = "";

		for (int i = 0; i < kids.length; i++) {
			String kid = transformToBox(kids[i]);
			kidsString += (" " + kid);
		}

		List<Rule> rules = findRules(ast);

		if (!rules.isEmpty()) {
			Rule found = matchRule(rules, ast);
			if (found != null) {
				return found.getBoxString();
			}
		}

		return "V [ " + kidsString + "]";
	}

	private Rule matchRule(List<Rule> rules, Object ast) {
		Iterator<Rule> iter = rules.iterator();
		
		while (iter.hasNext()) {
			Rule rule = iter.next();
			
			if (matcher.match(rule.getPatternAst(), ast)) {
				return rule;
			}
		}
		
		return null;
	}

	private List<Rule> findRules(Object ast) {
		Iterator<Rule> rules = spec.ruleIterator();
		List<Rule> selection = new LinkedList<Rule>();
		String outermost = adapter.getTypeOf(ast);

		while (rules.hasNext()) {
			Rule rule = rules.next();
			String ruleOutermost = adapter.getTypeOf(rule.getPatternAst());

			if (outermost.equals(ruleOutermost)) {
				selection.add(rule);
			}
		}

		return selection;
	}

}
