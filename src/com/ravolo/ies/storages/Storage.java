package com.ravolo.ies.storages;

import java.util.ArrayList;
import java.util.List;

import com.ravolo.ies.core.StorageOperation;

public abstract class Storage<E> {
	
	protected List<E> dataList;
	protected Class<E> clazz;
	protected int dataVersion;
	protected StorageOperation<E> operations;
	/**
	 * All storage will call init the init the settings
	 */
	public Storage(Class<E> clazz, int dataVersion) {
		this.clazz = clazz;
		this.dataVersion = dataVersion;
		init();
	}

	public void init() {
		dataList = new ArrayList<E>();
	}

	public List<E> getDataList() {
		return dataList;
	}

	public void setDataList(List<E> dataList) {
		this.dataList = dataList;
	}

	public Class<E> getClazz() {
		return clazz;
	}

	public int getDataVersion() {
		return dataVersion;
	}

	public void setOperations(StorageOperation<E> operations) {
		this.operations = operations;
	}

	
}
