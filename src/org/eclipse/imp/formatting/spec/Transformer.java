package org.eclipse.imp.formatting.spec;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	private IASTAdapter adapter;

	private Matcher matcher;

	private BoxStringBuilder builder;

	private BoxEnvironment boxes;

	private Map<String, List<Rule>> ruleMap;

	private Specification spec;

	public Transformer(Specification spec, IASTAdapter adapter) {
		this.adapter = adapter;
		this.matcher = new Matcher(adapter);
		this.builder = new BoxStringBuilder(adapter);
		this.boxes = new BoxEnvironment();
		this.spec = spec;
		this.ruleMap = new HashMap<String, List<Rule>>();
	}

	/**
	 * We fill a map to be able to quickly select rules that may be applicable.
	 * 
	 * @param spec
	 * @param adapter
	 */
	private void initializeRuleMap(Specification spec, IASTAdapter adapter) {
		ruleMap.clear();
		Iterator<Rule> iter = spec.ruleIterator();
		while (iter.hasNext()) {
			Rule rule = iter.next();
			Object pattern = rule.getPatternAst();

			if (pattern != null) {
				String outermost = adapter.getTypeOf(pattern);
				List<Rule> list = ruleMap.get(outermost);

				if (list == null) {
					list = new LinkedList<Rule>();
				}
				list.add(rule);

				this.ruleMap.put(outermost, list);
			}
		}
	}

	public String getBox(Object ast) {
		return boxes.get(ast);
	}

	/**
	 * Transforms an AST to a Box expression representing the source code of
	 * this AST.
	 * 
	 * @param source
	 * @param ast
	 * @return
	 */
	public String transformToBox(String source, Object ast) {
		initializeRuleMap(spec, adapter);
		transform(source, ast);
		String box = boxes.get(ast);
		boxes.clear();
		return box;
	}

	/**
	 * This method associates a box expression in String format with every AST
	 * node by applying the rules in the specification. It works bottom-up,
	 * which is necessary to make sure that for every variable bound by a
	 * pattern there is already a corresponding Box string associated in the
	 * this.boxes environment.
	 * 
	 * @param source
	 *            Used to construct default box expressions in absence of a rule
	 * @param ast
	 *            Used to apply the rules to.
	 */
	private void transform(String source, Object ast) {
		Object[] kids = adapter.getChildren(ast);

		if (kids.length == 0) {
			boxes.put(ast, builder.literal(source, ast));
			return;
		}

		for (int i = 0; i < kids.length; i++) {
			transform(source, kids[i]);
		}

		List<Rule> rules = findRules(ast);

		if (!rules.isEmpty()) {
			VariableEnvironment environment = new VariableEnvironment();
			Rule found = matchRule(rules, ast, environment);
			if (found != null) {
				String box = builder.substitute(found.getBoxString(),
						environment, boxes);
				boxes.put(ast, box);
				return;
			}
		}

		boxes.put(ast, builder.defaultWrapper(source, kids, boxes));
		return;
	}

	/**
	 * This method returns the first rule to match, and as a side effect the
	 * environment is filled.
	 * 
	 * @param rules
	 * @param ast
	 * @param environment
	 * @return
	 */
	private Rule matchRule(List<Rule> rules, Object ast,
			VariableEnvironment environment) {
		Iterator<Rule> iter = rules.iterator();

		while (iter.hasNext()) {
			Rule rule = iter.next();
			environment.clear();

			if (rule.getPatternAst() != null
					&& matcher.match(rule.getPatternAst(), ast, environment)) {
				return rule;
			}
		}

		return null;
	}

	/**
	 * Finds a list of rules that could match this AST. This pre-selection
	 * optimizes the transformation by considering only the rules that have a
	 * certain chance to match.
	 * 
	 * @param ast
	 * @return
	 */
	private List<Rule> findRules(Object ast) {
		List<Rule> rules = ruleMap.get(adapter.getTypeOf(ast));

		return rules != null ? rules : new LinkedList<Rule>();
	}

}
