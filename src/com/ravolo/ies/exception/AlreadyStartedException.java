package com.ravolo.ies.exception;

public class AlreadyStartedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6120278693314264755L;

	public AlreadyStartedException(){
		super("Process already started, you cannot edit the settings.");
	}
}
