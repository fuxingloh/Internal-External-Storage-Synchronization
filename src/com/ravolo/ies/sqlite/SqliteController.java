package com.ravolo.ies.sqlite;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
		ContentValues values = new ContentValues();
		for (SqliteField col : table.getFullFieldList()) {
			values.put(col.name, queryValues.get(col.name));
		}
		insert(values);
	}

	/**
	 * insert new data with content values
	 * 
	 * @param values
	 */
	public void insert(ContentValues values) {
		SQLiteDatabase database = this.getWritableDatabase();
		database.insert(table.getTableName(), null, values);
		database.close();
	}

	public void insertOrUpdate(ContentValues values) {
		SQLiteDatabase database = this.getWritableDatabase();
		int updatedRow = database.update(table.getTableName(), values,
				table.getPrimaryKey() + " = ?",
				new String[] { values.getAsString(table.getPrimaryKey()) });
		if (updatedRow == 0) {
			database.insert(table.getTableName(), null, values);// If cannot
																// find update
		}
		database.close();
	}

	public void insertOrUpdate(HashMap<String, String> queryValues) {
		ContentValues values = new ContentValues();
		for (SqliteField col : table.getFullFieldList()) {
			values.put(col.name, queryValues.get(col.name));
		}
		insertOrUpdate(values);
	}

	/**
	 * 
	 * @param queryValues
	 * @return
	 */
	public int updateRow(HashMap<String, String> queryValues) {
		ContentValues values = new ContentValues();
		for (SqliteField col : table.getFullFieldList()) {
			values.put(col.name, queryValues.get(col.name));
		}
		return updateRow(values);
	}

	public int updateRow(ContentValues values) {
		SQLiteDatabase database = this.getWritableDatabase();
		return database.update(table.getTableName(), values,
				table.getPrimaryKey() + " = ?",
				new String[] { values.getAsString(table.getPrimaryKey()) });
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
	public ArrayList<HashMap<String, String>> getAllAsString() {
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
	 * Return HashMap data in ArrayList
	 * 
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> getAllAsObject() {
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
		String selectQuery = "SELECT  * FROM " + table.getTableName();
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put(table.getPrimaryKey(), getObjectInCursor(cursor, 0));
				for (int i = 0; i < table.getFieldSize(); i++) {
					data.put(table.getField(i).name,
							getObjectInCursor(cursor, i + 1));
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
	public HashMap<String, String> getRowAsString(String id) {
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

	/**
	 * 
	 * @param id
	 * @return
	 */
	public HashMap<String, Object> getRowAsObject(String id) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + table.getTableName()
				+ " WHERE " + table.getPrimaryKey() + "='" + id + "'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				data.put(table.getPrimaryKey(), getObjectInCursor(cursor, 0));
				for (int i = 0; i < table.getFieldSize(); i++) {
					data.put(table.getField(i).name,
							getObjectInCursor(cursor, i + 1));
				}
			} while (cursor.moveToNext());
		}
		return data;
	}

	/**
	 * 
	 * @param cursor
	 * @param columnIndex
	 * @return
	 */
	Object getObjectInCursor(Cursor cursor, int columnIndex) {
		switch (cursor.getType(columnIndex)) {
		case Cursor.FIELD_TYPE_STRING:
			return cursor.getString(columnIndex);
		case Cursor.FIELD_TYPE_INTEGER:
			return cursor.getInt(columnIndex);
		case Cursor.FIELD_TYPE_FLOAT:
			return cursor.getDouble(columnIndex);
		case Cursor.FIELD_TYPE_BLOB:
			return cursor.getBlob(columnIndex);
		}
		return null;
	}

	/**
	 * use get as object or get as string instead
	 * @param id
	 * @return
	 */
	@Deprecated
	public HashMap<String, String> getRow(String id) {
		return getRowAsString(id);
	}

	/**
	 * use get as object or get as string instead
	 * @return
	 */
	@Deprecated
	public ArrayList<HashMap<String, String>> getAll() {
		return getAllAsString();
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
