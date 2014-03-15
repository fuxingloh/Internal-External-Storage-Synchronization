package com.ravolo.ies.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.google.common.collect.Lists;

public class ClassTool {
	public static Iterable<Field> getFieldsUpTo(Class<?> startClass,
			Class<?> exclusiveParent) {

		List<Field> currentClassFields = Lists.newArrayList(startClass
				.getDeclaredFields());
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null
				&& (exclusiveParent == null || !(parentClass
						.equals(exclusiveParent)))) {
			List<Field> parentClassFields = (List<Field>) getFieldsUpTo(
					parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

	public final static int OTHER = -2;
	public final static int GENERIC = -1;
	public final static int STRING = 0;
	public final static int CHAR = 1;
	public final static int INT = 2;
	public final static int DOUBLE = 3;
	public final static int LONG = 4;
	public final static int BYTE = 5;
	public final static int SHORT = 6;
	public final static int FLOAT = 7;
	public final static int BOOLEAN = 8;

	public static int getType(Field field) {
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			return GENERIC;
		} else {
			String typeName = field.getType().getSimpleName();
			if (typeName.equals("String")) {
				return STRING;
			} else if (typeName.equals("int")) {
				return INT;
			} else if (typeName.equals("char")) {
				return CHAR;
			} else if (typeName.equals("byte")) {
				return BYTE;
			} else if (typeName.equals("double")) {
				return DOUBLE;
			} else if (typeName.equals("short")) {
				return SHORT;
			} else if (typeName.equals("long")) {
				return LONG;
			} else if (typeName.equals("float")) {
				return FLOAT;
			} else if (typeName.equals("boolean")) {
				return BOOLEAN;
			} else {
				return OTHER;
			}
		}
	}

	public static boolean checkGenericExist(Field field) {
		return getType(field) == GENERIC;
	}

	public static void checkRawType(Field field) {
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) type;
			System.out.print("Raw type: " + pType.getRawType() + " - ");
			System.out.println("Type args: "
					+ pType.getActualTypeArguments()[0]);
		} else {

		}

	}

	public static boolean checkUnknown(Field field) {
		int fieldFlag = getType(field);
		return (fieldFlag == OTHER || fieldFlag == GENERIC);
	}
	
	
	public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }
}
