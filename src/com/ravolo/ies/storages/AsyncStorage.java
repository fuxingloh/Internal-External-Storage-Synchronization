package com.ravolo.ies.storages;

public interface AsyncStorage<E> {
	public void load(QueryOperationCallback callback);
	
	public void insert(E object,InsertOperationCallback callback);
	
	public void delete(E object,ChangeOperationCallback callback);
	
	public void update(E object,ChangeOperationCallback callback);
}
