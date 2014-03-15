package com.ravolo.ies.storages.internal;

import java.util.List;

import com.ravolo.ies.sqlite.SqliteAutoController;
import com.ravolo.ies.sqlite.SqliteController;
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
		sqlAutoController.insert(object);
		//sqlAutoController.l
		//TODO lookup
		return null;
	}

	@Override
	public boolean delete(E object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E update(E object) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
