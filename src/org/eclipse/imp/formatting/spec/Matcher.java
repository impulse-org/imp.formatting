package org.eclipse.imp.formatting.spec;

import org.eclipse.imp.xform.pattern.matching.IASTAdapter;

/*
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
	
	public boolean match(Object ast1, Object ast2) {
		String type1, type2;
		
		type1 = adapter.getTypeOf(ast1);
		type2 = adapter.getTypeOf(ast2);
		
		if (type1.equals(type2)) {
			Object[] kids1 = adapter.getChildren(ast1);
			Object[] kids2 = adapter.getChildren(ast2);
			
			assert kids1.length == kids2.length;
			
			for (int i = 0; i < kids1.length && i < kids2.length; i++) {
				if (!match(kids1[i], kids2[i])) {
					return false;
				}
			}
			
			return true;
		}
		else {
			return false;
		}
	}
	
}
