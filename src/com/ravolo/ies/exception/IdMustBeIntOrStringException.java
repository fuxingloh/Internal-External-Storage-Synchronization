package com.ravolo.ies.exception;

public class IdMustBeIntOrStringException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4420057867015092520L;

	public IdMustBeIntOrStringException(){
		super("Your Id must be text or string.");
	}
	
	public IdMustBeIntOrStringException(String message){
		super(message);
	}
}
