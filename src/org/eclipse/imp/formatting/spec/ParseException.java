package org.eclipse.imp.formatting.spec;

public class ParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3403628070858522497L;

	public ParseException(String message) {
		super(message);
	}
	
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}
}

