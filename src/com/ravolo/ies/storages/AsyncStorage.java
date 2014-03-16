package com.ravolo.ies.storages;

import java.util.ArrayList;
import java.util.List;

import com.ravolo.ies.core.StorageOperation;

public abstract class AsyncStorage<E> extends Storage<E> {

	public AsyncStorage(Class<E> clazz, StorageOperation<E> operations,
			int dataVersion) {
		super(clazz, operations, dataVersion);
	}

	public abstract void load(QueryOperationCallback<E> callback);

	public abstract void insert(E object, InsertOperationCallback<E> callback);

	public abstract void delete(E object, ChangeOperationCallback<E> callback);

	public abstract void update(E object, ChangeOperationCallback<E> callback);

	public List<E> insert(List<E> dataList) {
		final ArrayList<E> operatedDataList = new ArrayList<E>();
		for (E preInData : dataList) {
			insert(preInData, new InsertOperationCallback<E>() {
				@Override
				public void onCompleteInsert(E object) {
					operatedDataList.add(object);
				}
			});
		}
		return operatedDataList;
	}

	public void delete(List<E> dataList) {
		for (E preInData : dataList) {
			delete(preInData, new ChangeOperationCallback<E>() {
				@Override
				public void onComplete() {
				}

			});
		}
	}

	public void update(List<E> dataList) {
		for (E preInData : dataList) {
			update(preInData, new ChangeOperationCallback<E>() {
				@Override
				public void onComplete() {
				}

			});
		}
	}

}
