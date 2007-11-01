package org.eclipse.imp.formatting.spec;

import java.util.Iterator;

import org.eclipse.imp.xform.pattern.matching.IASTAdapter;

import lpg.runtime.IAst;

/**
 * I would like to make something that constructs Box AST's directly. For now,
 * I will build strings to represent the boxes, which will be parsed by the
 * Meta-Environment parser before formatting takes place.
 * @author jurgenv
 *
 */
public class BoxStringBuilder {
	private IASTAdapter adapter;

	public BoxStringBuilder(IASTAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * Construct a box literal.
	 * @param source
	 * @return
	 */
	public String literal(String source, Object ast) {
		int start = adapter.getOffset(ast);
		int end = start + adapter.getLength(ast);
		String term = source.substring(start, end);
		return "\"" + term.replaceAll("\n","\\\\n").replaceAll("\t","\\\\t") + "\"";
	}
	
	/**
	 * Substitutes the corresponding box values of ast variables in a box pattern
	 * @param pattern    the box string containing references to variables
	 * @param variables  the environment that maps ast variables to ast values
	 * @param boxes      the environment that maps ast values to box string
	 * @return
	 */
	public String substitute(String pattern, VariableEnvironment variables, BoxEnvironment boxes) {
		Iterator<Object> iter = variables.keySet().iterator();
		String current = pattern;

		while (iter.hasNext()) {
			Object var = iter.next();
			Object val = variables.get(var);
			String box = boxes.get(val);
			current = replaceAll(current, var.toString(), box);
		}

		return current;
	}

	private String replaceAll(String boxString, String var, String val) {
		int i;
		// there is some magic going on here, every variable in the box string
		// is surrounded by quotes. We adapt the value of i (+1 and -1) to remove
		// them on-the-fly. This makes the code brittle, since every variable now
		// HAS to be surrounded by double quotes
		while ((i = boxString.indexOf(var)) != -1) {
			boxString = boxString.substring(0, i - 1) + val
					+ boxString.substring(i + var.length() + 1);
		}
		return boxString;
	}

	/**
	 * Composes a vertical box around the boxes for the children of this node.
	 * This code is still buggy since literals and comments are not put in the
	 * box here.
	 * @param kids
	 * @param boxes
	 * @return
	 */
	public String defaultWrapper(String source, Object[] kids, BoxEnvironment boxes) {
		if (isCommaList(source, kids)) {
			return buildListBox("H", kids, boxes);
		}
		else if (isSemiColonList(source, kids)) {
			return buildListBox("V", kids, boxes);
		}
		else if (isExpressionStructured(kids)) {
			return buildBox("H", kids, boxes);
		}
		else if (isBlockStructured(kids)) {
			return buildBlock(kids, boxes);
		}
		else {
			return buildBox("V", kids, boxes);
		}
	}

	private boolean isSemiColonList(String source, Object[] kids) {
		return isSepList(source, kids, ";");
	}

	private boolean isCommaList(String source, Object[] kids) {
		return isSepList(source, kids, ",");
	}

	private boolean isSepList(String source, Object[] kids, String sep) {
		int len = kids.length;
		if (len == 3) {
			String lit = literal(source, kids[1]);
			
			if (lit.equals("\"" + sep + "\"")) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isExpressionStructured(Object[] kids) {
		int len = kids.length;
		
		if (len == 3) {
			if (adapter.getChildren(kids[1]).length == 0) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isBlockStructured(Object[] kids) {
		int len = kids.length;
		
		if (len >= 3) {
			Object first = kids[0];
			Object last = kids[len - 1];
			int lenFirst = adapter.getChildren(first).length;
			int lenLast = adapter.getChildren(last).length;
			
			if (lenFirst == 0 && lenLast == 0) {
				return true;
			}
		}
		
		return false;
	}

	private String buildBox(String op, Object[] kids, BoxEnvironment boxes) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(op + " [");
		for (int i = 0; i < kids.length; i++) {
			String box = boxes.get(kids[i]);
			assert box != null;
			buffer.append(box);
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	private String buildListBox(String op, Object[] kids, BoxEnvironment boxes) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(op + " [");
		
		for (int i = 0; i < kids.length - 1; i++) {
			String box = boxes.get(kids[i]);
			String sep = boxes.get(kids[++i]);
			assert box != null;
			buffer.append("H hs=0 [" + box + " " + sep + "]");
		}
		
		String box = boxes.get(kids[kids.length-1]);
		buffer.append(box);
		
		buffer.append("]");
		return buffer.toString();
	}

	private String buildBlock(Object[] kids, BoxEnvironment boxes) {
		assert kids.length >= 3;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("V [ ");
		buffer.append(boxes.get(kids[0]));
		buffer.append("I is=3 [ V [");
		
		for (int i = 1; i < kids.length - 1; i++) {
			String box = boxes.get(kids[i]);
			assert box != null;
			buffer.append(box);
		}
		
		buffer.append("] ]");
		buffer.append(boxes.get(kids[kids.length - 1]));
		buffer.append("]");
		return buffer.toString();
	}
	
}
