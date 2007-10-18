package org.eclipse.imp.formatting.spec;

import org.eclipse.imp.xform.pattern.matching.IASTAdapter;

/**
 * This class implements a simple AST pattern matcher. A pattern is
 * an AST with some placeholders. Currently, the placeholders are not
 * implemented, so in fact this matcher implements AST equality and
 * nothing more.
 */
public class Matcher {

	private IASTAdapter adapter;

	public Matcher(IASTAdapter adapter) {
		this.adapter = adapter;
	}

	private boolean nodeIdentityEqual(Object ast1, Object ast2) {
		String type1 = adapter.getTypeOf(ast1);
		String type2 = adapter.getTypeOf(ast2);

		System.err.println("----------");
		System.err.println("type1: " + type1);
		System.err.println("type2: " + type2);
		
		String name1 = (String) adapter.getValue(IASTAdapter.NAME, ast1);
		String name2 = (String) adapter.getValue(IASTAdapter.NAME, ast2);

		System.err.println("name1: " + name1);
		System.err.println("name2: " + name2);
		
		String kind1 = (String) adapter.getValue(IASTAdapter.KIND, ast1);
		String kind2 = (String) adapter.getValue(IASTAdapter.KIND, ast2);
		
		System.err.println("kind1: " + kind1);
		System.err.println("kind2: " + kind2);

		// TODO: the serious flaw here is that a nodes identity is not uniquely
		// defined by the attributes that I can get here generically. There are
		// hidden fields that contribute to the nodes identity and this will make
		// nodes match that should not match. I generic AST interface should have
		// access to ALL the identifying attributes of an AST node.

		return type1.equals(type2)
				&& ((name1 == null && name2 == null) || name1.equals(name2))
				&& ((kind1 == null && name2 == null) || kind1.equals(kind2));
	}

	public boolean match(Object ast1, Object ast2) {
		if (nodeIdentityEqual(ast1, ast2)) {
			Object[] kids1 = adapter.getChildren(ast1);
			Object[] kids2 = adapter.getChildren(ast2);

			System.err.println("kids1: " + kids1.length);
			System.err.println("kids2: " + kids2.length);
			
			if (kids1.length != kids2.length) {
				return false;
			}

			for (int i = 0; i < kids1.length && i < kids2.length; i++) {
				if (!match(kids1[i], kids2[i])) {
					return false;
				}
			}

			return true;
		}
		return false;
	}
}
