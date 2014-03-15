package com.ravolo.ies.sqlite;

import java.util.ArrayList;
import java.util.Collection;

import com.ravolo.ies.exception.NullPrimaryKeyException;

/**
 * Table Class
 * 
 * @author Fuxing
 * 
 */
public class SqliteTable {

	private final int tableVersion;
	private final String dbName;
	private final String tableName;
	private String primaryName;
	private String updateStatement;

	private ArrayList<SqliteField> columnList;

	/**
	 * 
	 * @param dbName
	 * @param tableName
	 * @param tableVersion
	 */
	public SqliteTable(String dbName, String tableName, int tableVersion) {
		this.dbName = dbName;
		this.tableName = tableName;
		this.tableVersion = tableVersion;
		columnList = new ArrayList<SqliteField>();
	}

	/**
	 * 
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * 
	 * @return
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * 
	 * @return
	 */
	public String getPrimaryKey() {
		if (primaryName == null) {
			throw new NullPrimaryKeyException();
		}
		return primaryName.split(" ")[0];
	}

	/**
	 * 
	 * @return
	 */
	public String getPrimaryKeyCreate() {
		if (primaryName == null) {
			throw new NullPrimaryKeyException();
		}
		return primaryName;
	}

	/**
	 * 
	 * @param primaryName
	 */
	public void setPrimaryText(String primaryName) {
		this.primaryName = primaryName + " TEXT";
	}

	/**
	 * 
	 * @param primaryName
	 */
	public void setPrimaryInteger(String primaryName) {
		this.primaryName = primaryName + " INTEGER";
	}
	
	void setPrimary(String full){
		this.primaryName = full;
	}

	/**
	 * 
	 * @return
	 */
	public int getTableVersion() {
		return tableVersion;
	}

	/**
	 * 
	 * @param name
	 */
	public void addText(String name) {
		columnList.add(new SqliteField(name, "TEXT"));
	}

	/**
	 * 
	 * @param name
	 */
	public void addInt(String name) {
		columnList.add(new SqliteField(name, "INTEGER"));
	}

	/**
	 * Set and add the column yourself.
	 * @param column
	 */
	public void addField(SqliteField column) {
		columnList.add(column);

	}
	
	void addAllField(Collection<? extends SqliteField> storageList){
		columnList.addAll(storageList);
	}

	/**
	 * 
	 * @return
	 */

	public int getFieldSize() {
		return columnList.size();
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<SqliteField> getFieldList() {
		return columnList;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<SqliteField> getFullFieldList() {
		ArrayList<SqliteField> columnList = new ArrayList<SqliteField>();
		columnList.addAll(this.columnList);
		String primary = "";
		if (primaryName.contains("INTEGER")) {
			primary = primaryName.replaceAll("INTEGER", "").trim();
			columnList.add(new SqliteField(primary, "INTEGER"));
		} else if (primaryName.contains("TEXT")) {
			primary = primaryName.replaceAll("TEXT", "").trim();
			columnList.add(new SqliteField(primary, "TEXT"));
		}
		return columnList;
	}

	/**
	 * 
	 * @return
	 */
	public String getUpdateStatement() {
		return updateStatement;
	}

	/**
	 * 
	 * @param updateStatement
	 */
	public void setUpdateStatement(String updateStatement) {
		this.updateStatement = updateStatement;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public SqliteField getField(int i) {
		return columnList.get(i);
	}

}
