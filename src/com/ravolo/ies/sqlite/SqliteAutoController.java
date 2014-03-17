package com.ravolo.ies.sqlite;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.objenesis.ObjenesisHelper;

import android.content.ContentValues;
import android.content.Context;

import com.google.common.collect.Lists;
import com.ravolo.ies.annotations.StorageBlob;
import com.ravolo.ies.annotations.StorageDoNotInclude;
import com.ravolo.ies.annotations.StorageName;
import com.ravolo.ies.exception.IdMustBeIntOrStringException;
import com.ravolo.ies.exception.NonExistenceIdException;
import com.ravolo.ies.sqlite.util.SqliteTool;
import com.ravolo.ies.util.ClassTool;

public class SqliteAutoController<E> {
	private SqliteController mainSqlController;

	private Context context;

	private ArrayList<SqliteField> storageList;
	private SqliteField primaryKeyField;
	private Field primaryField;

	private ArrayList<Field> storageFieldList;

	/**
	 * Class file this thing is working on
	 */
	private Class<E> clazz;

	public SqliteAutoController(Context context, Class<E> clazz, int version) {
		this.context = context;
		this.clazz = clazz;
		init(version);
	}

	void init(int version) {
		storageFieldList = new ArrayList<Field>();
		this.generateStorageData();
		this.initDatabase(version);
	}

	/**
	 * Generate storage list data
	 */
	private void generateStorageData() {
		storageList = new ArrayList<SqliteField>();
		ArrayList<Field> fields = Lists.newArrayList(ClassTool.getFieldsUpTo(
				clazz, Object.class));
		int IdNo = 0;
		for (Field field : fields) {
			boolean annotationContainedStorageName = false;
			boolean doNotInclude = false;
			boolean storeThisAsBlob = false;
			String name = null;
			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof StorageName) {
					annotationContainedStorageName = true;
					StorageName storageName = (StorageName) annotation;
					name = storageName.value();
				} else if (annotation instanceof StorageDoNotInclude) {
					doNotInclude = true;
					break;
				} else if (annotation instanceof StorageBlob) {
					storeThisAsBlob = true;
				}
			}
			// field.get
			if (!doNotInclude) {
				field.setAccessible(true);
				// Must include
				// start off by getting the name.
				if (!annotationContainedStorageName) {
					name = field.getName();
				}
				if (!name.equalsIgnoreCase("id")) {
					// not primary id
					// Check what type of data it is here
					if (storeThisAsBlob) {
						// Store this as blob?
						storageList.add(generateBlobField(name, field));
					} else {
						// Store as normal if cannot find type to store, do not
						// store.
						SqliteField sqlField = generateField(name, field);
						if (sqlField != null) {
							storageList.add(sqlField);
							storageFieldList.add(field);
						}
					}
				} else {
					// is primary id
					IdNo++;
					switch (ClassTool.getType(field)) {
					case ClassTool.INT:
						primaryKeyField = SqliteTool.toField(name, field);
						break;
					case ClassTool.STRING:
						throw new IdMustBeIntOrStringException("Id can only be int.");
						//primaryKeyField = SqliteTool.toField(name, field);
						//break;
					default:
						throw new IdMustBeIntOrStringException();
					}
					primaryField = field;
				}
			}
		}
		if (IdNo != 1) {
			throw new NonExistenceIdException();
		}
	}

	private SqliteField generateField(String name, Field field) {
		return SqliteTool.toField(name, field);
	}

	private SqliteField generateBlobField(String name, Field field) {
		return SqliteTool.toBlobField(name, field);
	}

	private void initDatabase(int version) {
		// use package name as db name
		String dbName = clazz.getPackage().getName();
		String[] packages = dbName.split(Pattern.quote("."));
		dbName = "";
		for (int i = 0; i < packages.length && i < 3; i++) {
			dbName += packages[i];
			if (i < 2) {
				dbName += ".";
			}
		}
		SqliteTable table = new SqliteTable(dbName, clazz.getSimpleName(),
				version);
		String full = primaryKeyField.getName() + " "
				+ primaryKeyField.getType();
		table.setPrimary(full);
		table.addAllField(storageList);
		mainSqlController = new SqliteController(context, table);
	}

	/**
	 * Insert object into data base
	 * 
	 * @param object
	 */
	public E insert(E object) {
		ContentValues cv = getContentValues(object);
		cv.remove(primaryKeyField.name);
		long id = mainSqlController.insert(cv);
		primaryField.setAccessible(true);
		try {
			if (primaryKeyField.type.equals(SqliteField.TEXT)) {
				// Text
				primaryField.set(object,id+"");
			} else if (primaryKeyField.type.equals(SqliteField.INTEGER)) {
				// Integer
				primaryField.setInt(object,(int) id);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
		return object;
	}

	public ContentValues getContentValues(E object) {
		ContentValues values = new ContentValues();
		try {
			// primary key first
			if (primaryKeyField.type.equals(SqliteField.TEXT)) {
				// Text
				values.put(primaryKeyField.name,
						(String) primaryField.get(object));
			} else if (primaryKeyField.type.equals(SqliteField.INTEGER)) {
				// Integer
				values.put(primaryKeyField.name, primaryField.getInt(object));
			}
			for (int i = 0; i < storageList.size(); i++) {
				SqliteField sqlField = storageList.get(i);
				Field field = storageFieldList.get(i);
				// put the data in content vlaues
				if (sqlField.type.equals(SqliteField.TEXT)) {
					values.put(sqlField.name, (String) field.get(object));
				} else if (sqlField.type.equals(SqliteField.INTEGER)) {
					values.put(sqlField.name, field.getInt(object));
				} else if (sqlField.type.equals(SqliteField.REAL)) {
					values.put(sqlField.name, field.getDouble(object));
				} else if (sqlField.type.equals(SqliteField.BLOB)) {
					values.put(sqlField.name,
							ClassTool.serialize(field.get(object)));
				}
			}
			return values;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(E object) {
		try {
			if (primaryKeyField.type.equals(SqliteField.TEXT)) {
				// Text
				delete((String) primaryField.get(object));
			} else if (primaryKeyField.type.equals(SqliteField.INTEGER)) {
				// Integer
				delete(primaryField.getInt(object));
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(String id) {
		// string will be the main 1
		mainSqlController.deleteRow(id);
	}

	public void delete(int id) {
		delete(id + "");
	}

	public void update(E object) {
		mainSqlController.updateRow(getContentValues(object));
	}

	public List<E> getAll() {
		List<HashMap<String, Object>> dataList = mainSqlController
				.getAllAsObject();
		List<E> list = new ArrayList<E>();
		for (HashMap<String, Object> data : dataList) {
			list.add(convertToObject(data));
		}
		return list;
	}

	public E lookUp(String id) {
		return convertToObject(mainSqlController.getRowAsObject(id));
	}
	
	public E lookUp(int id) {
		return lookUp(id+"");
	}
	
	/**
	 * Execument query statement
	 * @param statement
	 */
	public void executeQuery(String statement){
		mainSqlController.execSQL(statement);
	}

	E convertToObject(HashMap<String, Object> values) {
		try {
			E object = ObjenesisHelper.newInstance(clazz);
			// set primary first
			String pActualName = primaryField.getName();
			Field pField = object.getClass().getDeclaredField(pActualName);
			pField.setAccessible(true);
			if (primaryKeyField.type.equals(SqliteField.TEXT)) {
				// Text
				pField.set(object, values.get(primaryKeyField.name)+"");
			} else if (primaryKeyField.type.equals(SqliteField.INTEGER)) {
				// Integer
				pField.setInt(object,
						(Integer) values.get(primaryKeyField.name));
			}
			for (int i = 0; i < storageList.size(); i++) {
				SqliteField sqlField = storageList.get(i);
				Field classBasedField = storageFieldList.get(i);
				String actualName = classBasedField.getName();
				Field field = object.getClass().getDeclaredField(actualName);
				// put the data in content vlaues
				Object innerObj = SqliteTool.deserialize(sqlField.name,
						values.get(sqlField.name));
				field.setAccessible(true);
				field.set(object, innerObj);
			}
			return object;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the db controller used here
	 * 
	 * @return
	 */
	public SqliteController getSqlController() {
		return mainSqlController;
	}

	/**
	 * 
	 * @return
	 */
	public SqliteTable getTable() {
		return mainSqlController.getTable();
	}

	/**
	 * 
	 * @return
	 */
	public int getVersion() {
		return getTable().getTableVersion();
	}
}
