package com.ravolo.ies.sqlite;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteController extends SQLiteOpenHelper {
	/**
	 * Main pillar to this class
	 */
	private SqliteTable table;

	/**
	 * Create Simple DB controller with table object passed in
	 * 
	 * @param applicationcontext
	 * @param table
	 */
	public SqliteController(Context applicationcontext, SqliteTable table) {
		super(applicationcontext, table.getDbName() + ".db", null, table
				.getTableVersion());
		this.table = table;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String query;
		query = "CREATE TABLE " + table.getTableName() + " ( "
				+ table.getPrimaryKeyCreate() + " PRIMARY KEY";
		for (SqliteField col : table.getFieldList()) {
			query += col.getCreateString();
		}
		query += " )";
		database.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int version_old,
			int current_version) {
		String query;
		if (table.getUpdateStatement() != null) {
			query = table.getUpdateStatement();
			database.execSQL(query);
		} else {
			query = "DROP TABLE IF EXISTS " + table.getTableName();
			onCreate(database);
		}
	}

	/**
	 * Insert New Data
	 * 
	 * @param queryValues
	 */
	public void insert(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		for (SqliteField col : table.getFullFieldList()) {
			values.put(col.name, queryValues.get(col.name));
		}
		database.insert(table.getTableName(), null, values);
		database.close();
	}

	public void insertOrUpdate(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		for (SqliteField col : table.getFullFieldList()) {
			values.put(col.name, queryValues.get(col.name));
		}
		int updatedRow = database.update(table.getTableName(), values,
				table.getPrimaryKey() + " = ?",
				new String[] { queryValues.get(table.getPrimaryKey()) });
		if (updatedRow == 0) {
			database.insert(table.getTableName(), null, values);// If cannot
																// find update
		}
		database.close();
	}

	/**
	 * 
	 * @param queryValues
	 * @return
	 */
	public int updateRow(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		for (SqliteField col : table.getFieldList()) {
			values.put(col.name, queryValues.get(col.name));
		}
		return database.update(table.getTableName(), values,
				table.getPrimaryKey() + " = ?",
				new String[] { queryValues.get(table.getPrimaryKey()) });
	}

	/**
	 * 
	 * @param queryValues
	 */
	public void deleteAndInsert(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		for (SqliteField col : table.getFullFieldList()) {
			values.put(col.name, queryValues.get(col.name));
		}
		database.delete(table.getTableName(), table.getPrimaryKey() + " = ?",
				new String[] { queryValues.get(table.getPrimaryKey()) });
		database.insert(table.getTableName(), null, values);
		database.close();

	}

	/**
	 * 
	 * @param id
	 */
	public void deleteRow(String id) {
		SQLiteDatabase database = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM  " + table.getTableName() + " where "
				+ table.getPrimaryKey() + "='" + id + "'";
		database.execSQL(deleteQuery);
	}

	/**
	 * Return HashMap data in ArrayList
	 * 
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getAll() {
		ArrayList<HashMap<String, String>> dataList;
		dataList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM " + table.getTableName();
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put(table.getPrimaryKey(), cursor.getString(0));
				for (int i = 0; i < table.getFieldSize(); i++) {
					data.put(table.getField(i).name, cursor.getString(i + 1));
				}
				dataList.add(data);
			} while (cursor.moveToNext());
		}
		return dataList;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getRow(String id) {
		HashMap<String, String> data = new HashMap<String, String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + table.getTableName()
				+ " WHERE " + table.getPrimaryKey() + "='" + id + "'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				data.put(table.getPrimaryKey(), cursor.getString(0));
				for (int i = 0; i < table.getFieldSize(); i++) {
					data.put(table.getField(i).name, cursor.getString(i + 1));
				}
			} while (cursor.moveToNext());
		}
		return data;
	}

	public void execSQL(String statement) {
		SQLiteDatabase database = this.getReadableDatabase();
		database.execSQL(statement);
		database.close();
	}

	public void deleteAllRow() {
		execSQL("delete from " + table.getTableName());
	}

	public SqliteTable getTable() {
		return table;
	}
	
	
}
