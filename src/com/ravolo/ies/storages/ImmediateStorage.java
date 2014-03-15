package com.ravolo.ies.storages;

import java.util.List;

public interface ImmediateStorage<E> {

	public List<E> load();
	
	public E insert(E object);
	
	public boolean delete(E object);
	
	public boolean update(E object);
}
