package org.eclipse.imp.formatting.spec;

import java.text.BreakIterator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lpg.runtime.IAst;

import org.eclipse.imp.xform.pattern.matching.IASTAdapter;

/**
 * This class maps an arbitrary AST to a Box term in String form. It works by
 * recursively applying a set of rules to an AST. The rules have AST patterns
 * that are matched against the actual AST, if the match succeeds, the rule has
 * a reference to a Box pattern for building up the Box term. If there is no
 * rule for a certain part of an AST, this transformer picks a default Box
 * representation.
 * 
 * It is guaranteed that this transformer produces a fully valid Box term.
 * 
 * @author jurgenv
 * 
 */
public class Transformer {
	private Specification spec;

	private IASTAdapter adapter;

	private Matcher matcher;

	public Transformer(Specification spec, IASTAdapter adapter) {
		this.spec = spec;
		this.adapter = adapter;
		this.matcher = new Matcher(adapter);
	}

	public String transformToBox(String source, Object ast) {
		Object[] kids = adapter.getChildren(ast);
		String[] newkids = new String[kids.length];
		
		if (kids.length == 0) {
			return sourceTextBox(source, ast);
		}

		for (int i = 0; i < kids.length; i++) {
			newkids[i] = transformToBox(source, kids[i]);
		}

		List<Rule> rules = findRules(ast);

		// TODO: add support for meta variables
		if (!rules.isEmpty()) {
			Rule found = matchRule(rules, ast);
			if (found != null) {
				return found.getBoxString();
			}
		}
		
		// TODO: figure out a smarter default, this will not work right
		// with nested applications of rules
		return sourceTextBox(source, ast);
	}

	private String sourceTextBox(String source, Object ast) {
		int offset = adapter.getOffset(ast);
		int length = adapter.getLength(ast);
		return "\"" + source.substring(offset, offset + length).replaceAll("\n","\\\\n").replaceAll("\t","\\\\t") + "\"";
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
