/*******************************************************************************
* Copyright (c) 2008 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jurgen Vinju (jurgenv@cwi.nl) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.formatting.spec;

import java.util.HashMap;

/** 
 * This class implements a mapping from ast nodes that represent variables
 * to actual ast value nodes. This environment is typically build during a
 * match, and used later to implement substitutions.
 * 
 * @author jurgenv
 *
 */
public class VariableEnvironment extends HashMap<Object,Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
