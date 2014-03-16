package com.ravolo.ies.storages;

import java.util.ArrayList;
import java.util.List;

import com.ravolo.ies.core.StorageOperation;

public abstract class ImmediateStorage<E> extends Storage<E> {

	public ImmediateStorage(Class<E> clazz,StorageOperation<E> operations, int dataVersion) {
		super(clazz, operations, dataVersion);
	}

	public abstract List<E> load();

	public abstract E insert(E object);

	public abstract boolean delete(E object);

	public abstract boolean update(E object);
	
	public List<E> insert(List<E> dataList){
		ArrayList<E> operatedDataList = new ArrayList<E>();
		for(E preInData:dataList){
			operatedDataList.add(insert(preInData));
		}
		return operatedDataList;
	}
	
	public void delete(List<E> dataList){
		for(E preInData:dataList){
			delete(preInData);
		}
	}
	
	public void update(List<E> dataList){
		for(E preInData:dataList){
			update(preInData);
		}
	}
}
