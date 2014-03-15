package com.ravolo.ies.storages;

public interface AsyncStorage<E> {
	public void load();
	
	public void insert(E object);
	
	public void delete(E object);
	
	public void update(E object);
}
