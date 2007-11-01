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
