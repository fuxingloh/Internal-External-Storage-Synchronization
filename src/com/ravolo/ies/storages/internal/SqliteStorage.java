package com.ravolo.ies.storages.internal;

import java.util.List;

import android.content.Context;

import com.ravolo.ies.sqlite.SqliteAutoController;
import com.ravolo.ies.storages.ImmediateStorage;

public class SqliteStorage<E> extends ImmediateStorage<E> {

	private SqliteAutoController<E> sqlAutoController;

	public SqliteStorage(Class<E> clazz, int dataVersion,Context context) {
		super(clazz, dataVersion);
		sqlAutoController = new SqliteAutoController<E>(context, clazz,
				dataVersion);
	}

	@Override
	public List<E> load() {
		return sqlAutoController.getAll();
	}

	@Override
	public E insert(E object) {
		return sqlAutoController.insert(object);
	}

	@Override
	public boolean delete(E object) {
		sqlAutoController.delete(object);
		return true;
	}

	@Override
	public boolean update(E object) {
		sqlAutoController.update(object);
		return true;
	}

}
