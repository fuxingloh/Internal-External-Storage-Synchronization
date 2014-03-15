package com.ravolo.ies.storages;

import java.util.ArrayList;
import java.util.List;

public abstract class Storage<E> {
	
	protected List<E> dataList;
	protected Class<E> clazz;
	protected int dataVersion;
	/**
	 * All storage will call init the init the settings
	 */
	public Storage(Class<E> clazz,int dataVersion) {
		init();
		this.clazz = clazz;
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

}
