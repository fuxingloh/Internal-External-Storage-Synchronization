package com.ravolo.ies.exception;

public class NullPrimaryKeyException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3850664757025534284L;
	public NullPrimaryKeyException() {
		super("Primary Key for the table wasn't set.");
	}

	public NullPrimaryKeyException(String message) {
		super(message);
	}
}
