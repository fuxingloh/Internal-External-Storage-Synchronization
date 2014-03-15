package com.ravolo.ies.storages.external;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.ravolo.ies.storages.AsyncStorage;
import com.ravolo.ies.storages.ChangeOperationCallback;
import com.ravolo.ies.storages.InsertOperationCallback;
import com.ravolo.ies.storages.QueryOperationCallback;
import com.ravolo.ies.storages.Storage;

public class AzureMobileServiceStorage<E> extends Storage<E> implements AsyncStorage<E>{

	protected MobileServiceClient client;
	public AzureMobileServiceStorage(Class<E> clazz, int dataVersion,MobileServiceClient client) {
		super(clazz, dataVersion);
		this.client = client;
	}
	@Override
	public void load(QueryOperationCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void insert(E object, InsertOperationCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void delete(E object, ChangeOperationCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void update(E object, ChangeOperationCallback callback) {
		// TODO Auto-generated method stub
		
	}


}
