package com.ravolo.ies.sqlite;

public class SqliteField {
	public final static String TEXT = "TEXT";
	public final static String INTEGER = "INTEGER";
	public final static String REAL = "REAL";
	public final static String BLOB = "BLOB";
	
	public String name;
	public String type;// is it integer or text?

	/**
	 * 
	 * @param name
	 * @param type
	 */
	public SqliteField(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public String getCreateString() {
		return ", " + name + " " + type;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
}
