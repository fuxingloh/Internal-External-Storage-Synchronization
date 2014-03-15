package com.ravolo.ies.storages.internal;

import java.util.List;

import com.ravolo.ies.sqlite.SqliteAutoController;
import com.ravolo.ies.storages.ImmediateStorage;
import com.ravolo.ies.storages.Storage;

public class SqliteStorage<E> extends Storage<E> implements ImmediateStorage<E> {

	private SqliteAutoController<E> sqlAutoController;
	public SqliteStorage(Class<E> clazz,int dataVersion){
		super(clazz, dataVersion);
	}
	public void init(){
		super.init();
		sqlAutoController = new SqliteAutoController<E>(null,clazz,dataVersion);
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
