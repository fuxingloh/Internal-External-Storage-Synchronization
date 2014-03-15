package com.ravolo.ies.exception;

public class NonExistenceIdException extends NullPrimaryKeyException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6188905678354067106L;

	public NonExistenceIdException() {
		super(
				"Your object that is using the SqliteAutoController need to have a primary id field. "
						+ "You can use @com.ravolo.ies.annotations.StorageName(\"id\") to declare. "
						+ "You can only have 1 id.");
	}
}
