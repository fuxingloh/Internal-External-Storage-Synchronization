package com.ravolo.ies.storages;

import java.util.List;

public interface QueryOperationCallback<E>{
	public void onQueryComplete(List<E> dataList);
}