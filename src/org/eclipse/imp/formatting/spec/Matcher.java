package org.eclipse.imp.formatting.spec;

import java.util.Iterator;
import java.util.Map;

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
	
	private boolean isVariableName(String name) {
		return name.endsWith(">") && name.startsWith("<");
	}
	
	private boolean isVariableAst(Object pattern) {
		return isVariableName(pattern.toString());
	}

	private boolean nodeIdentityMatch(Object pattern, Object object, Map<String, Object> environment) {
		String type1 = adapter.getTypeOf(pattern);
		String type2 = adapter.getTypeOf(object);

		System.err.println("----------");
		System.err.println("type1: " + type1);
		System.err.println("type2: " + type2);
		
		String name1 = (String) adapter.getValue(IASTAdapter.NAME, pattern);
		String name2 = (String) adapter.getValue(IASTAdapter.NAME, object);
		
		System.err.println("name1: " + name1);
		System.err.println("name2: " + name2);
		
		String kind1 = (String) adapter.getValue(IASTAdapter.KIND, pattern);
		String kind2 = (String) adapter.getValue(IASTAdapter.KIND, object);
		
		System.err.println("kind1: " + kind1);
		System.err.println("kind2: " + kind2);

		// TODO: the serious flaw here is that a nodes identity is not uniquely
		// defined by the attributes that I can get here generically. There are
		// hidden fields that contribute to the nodes identity and this will make
		// nodes match that should not match. I generic AST interface should have
		// access to ALL the identifying attributes of an AST node.

		if (isVariableAst(pattern)) {
			if (kind1.equals(kind2)) {
			  environment.put(pattern.toString(), object);
			  return true;
			}
			else {
				return false;
			}
		}
		else {
			return typeMatch(type1, type2) && nameMatch(name1, name2, environment) && kindMatch(kind1, kind2);
		}
	}

	private boolean kindMatch(String kind1, String kind2) {
		return (kind1 == null && kind2 == null) || kind1.equals(kind2);
	}

	private boolean nameMatch(String name1, String name2, Map<String, Object> environment) {
		if (name1 == null && name2 == null) {
			return true;
		}
		else if (name1 == null || name2 == null) {
			return false;
		}
		else if (isVariableName(name1)) {
			environment.put(name1, name2);
			return true;
		}
		else {
			return name1.equals(name2);
		}
	}

	private boolean typeMatch(String type1, String type2) {
		return (type1 == null && type2 == null) || type1.equals(type2);
	}

	public boolean match(Object pattern, Object object, Map<String, Object> environment) {
		if (nodeIdentityMatch(pattern, object, environment)) {
			Object[] kids1 = adapter.getChildren(pattern);
			Object[] kids2 = adapter.getChildren(object);

			System.err.println("kids1: " + kids1.length);
			System.err.println("kids2: " + kids2.length);
			
			if (kids1.length != kids2.length) {
				return false;
			}

			for (int i = 0; i < kids1.length && i < kids2.length; i++) {
				if (!match(kids1[i], kids2[i], environment)) {
					return false;
				}
			}

			return true;
		}
		return false;
	}

	public String substitute(String boxString, Map<String,Object> environment) {
		Iterator<String> iter = environment.keySet().iterator();
		
		while (iter.hasNext()) {
			String var = iter.next();
			String val = environment.get(var).toString();
			boxString = replaceAll(boxString, var, val);
		}
		
		return boxString;
	}

	private String replaceAll(String boxString, String var, String val) {
		int i;
		while ((i = boxString.indexOf(var)) != -1) {
			boxString = boxString.substring(0, i) + val + boxString.substring(i+var.length());
		}
		return boxString;
	}
}
