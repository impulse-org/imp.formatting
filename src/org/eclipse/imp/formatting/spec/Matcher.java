/*******************************************************************************
* Copyright (c) IBM Corporation 2008 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jurgen Vinju (jurgenv@cwi.nl) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.formatting.spec;

import org.eclipse.imp.services.IASTAdapter;

/**
 * This class implements a simple AST pattern matcher. 
 */
public class Matcher {

	private IASTAdapter adapter;

	public Matcher(IASTAdapter adapter) {
		this.adapter = adapter;
	}

	private boolean nodeIdentityMatch(Object pattern, Object object) {
		String type1 = adapter.getTypeOf(pattern);
		String type2 = adapter.getTypeOf(object);

		return typeMatch(type1, type2);
	}

	private boolean typeMatch(String type1, String type2) {
		return (type1 == null && type2 == null) || type1.equals(type2);
	}

	
	public boolean match(Object pattern, Object object, VariableEnvironment environment) {
//		System.err.println("matching object: " + object);
//		System.err.println("to pattern: " + pattern);
		
		if (adapter.isMetaVariable(pattern)) {
			environment.put(pattern.toString(), object);
			return true;
		}
		else if (nodeIdentityMatch(pattern, object)) {
			Object[] kids1 = adapter.getChildren(pattern);
			Object[] kids2 = adapter.getChildren(object);

			if (kids1.length != kids2.length) {
				return false;
			}
			
			if (kids1.length == 0) {
				return pattern.equals(object);
			}
			
			for (int i = 0; i < kids1.length; i++) {
				if (!match(kids1[i], kids2[i], environment)) {
					return false;
				}
			}

			environment.putAll(environment);
			return true;
		}
			
		return false;
	}

	
}
