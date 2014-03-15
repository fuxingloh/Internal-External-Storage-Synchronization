package com.ravolo.ies.sqlite.util;

import java.io.IOException;
import java.lang.reflect.Field;

import com.ravolo.ies.sqlite.SqliteField;
import com.ravolo.ies.util.ClassTool;

public class SqliteTool {
	public static final int TEXT = 0;
	public static final int INTEGER = 1;
	public static final int REAL = 2;
	public static final int BLOB = 3;

	public static final String divider = "_Type";

	public static SqliteField toField(String name, Field field) {
		name += divider + ClassTool.getType(field);
		String type = null;
		switch (checkStoreFieldType(field)) {
		case TEXT:
			type = SqliteField.TEXT;
		case INTEGER:
			type = SqliteField.INTEGER;
		case REAL:
			type = SqliteField.REAL;
		case BLOB:
			type = SqliteField.BLOB;
		case -1:
			return null;// unknown type
		}
		SqliteField sqlField = new SqliteField(name, type);
		return sqlField;
	}

	public static SqliteField toBlobField(String name, Field field) {
		name += divider + field.getType().getSimpleName();
		SqliteField sqlField = new SqliteField(name, SqliteField.BLOB);
		return sqlField;
	}

	public static int checkStoreFieldType(Field field) {
		switch (ClassTool.getType(field)) {
		case ClassTool.STRING:
		case ClassTool.CHAR:
			return TEXT;
		case ClassTool.INT:
		case ClassTool.BYTE:
		case ClassTool.SHORT:
		case ClassTool.LONG:
			return INTEGER;
		case ClassTool.FLOAT:
		case ClassTool.DOUBLE:
			return REAL;
		case ClassTool.BOOLEAN:
			return BLOB;
		default:
			return -1;
		}
	}

	public static Object deserialize(String name, Object object) throws ClassNotFoundException, IOException {
		Object obj = null;
		String[] fullTypes = name.split(divider);
		String type = fullTypes[fullTypes.length - 1];
		// check if normal type
		int basicFieldType = Integer.parseInt(type);
		switch (basicFieldType) {

		
		case ClassTool.STRING:
		case ClassTool.CHAR:
			return object;
		case ClassTool.INT:
		case ClassTool.BYTE:
		case ClassTool.SHORT:
		case ClassTool.LONG:
			return object;
		case ClassTool.FLOAT:
		case ClassTool.DOUBLE:
			return object;
		case ClassTool.BOOLEAN:
			obj = ClassTool.deserialize((byte[]) object);
			break;
		case ClassTool.GENERIC:
		case ClassTool.OTHER:
		default:
			obj = ClassTool.deserialize((byte[]) object);
		}
		// not normal deserialize it

		return obj;
	}

}
