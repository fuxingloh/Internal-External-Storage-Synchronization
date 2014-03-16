package com.ravolo.ies.storages.external;

import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.ravolo.ies.core.StorageOperation;
import com.ravolo.ies.storages.AsyncStorage;
import com.ravolo.ies.storages.ChangeOperationCallback;
import com.ravolo.ies.storages.InsertOperationCallback;
import com.ravolo.ies.storages.QueryOperationCallback;

public class AzureMobileServiceStorage<E> extends AsyncStorage<E> {

	private MobileServiceClient client;
	private MobileServiceTable<E> table;
	private String tableName;

	public AzureMobileServiceStorage(Class<E> clazz,
			StorageOperation<E> operations, int dataVersion,
			MobileServiceClient client) {
		super(clazz, operations, dataVersion);
		this.client = client;
		this.table = this.client.getTable(clazz);
	}

	/**
	 * Overloaded constructor, if it is a custom tableName
	 * 
	 * @param clazz
	 * @param dataVersion
	 * @param client
	 * @param tableName
	 */
	public AzureMobileServiceStorage(Class<E> clazz,
			StorageOperation<E> operations, int dataVersion,
			MobileServiceClient client, String tableName) {
		super(clazz, operations, dataVersion);
		this.client = client;
		this.tableName = tableName;
		this.table = this.client.getTable(this.tableName, clazz);
	}

	@Override
	public void load(final QueryOperationCallback<E> callback) {
		table.execute(new TableQueryCallback<E>() {
			@Override
			public void onCompleted(List<E> dataList, int arg1,
					Exception exception, ServiceFilterResponse arg3) {
				if (exception == null) {
					callback.onQueryComplete(dataList);
				} else {
					operations.onException(exception,
							StorageOperation.EXTERNAL_LOAD);
				}

			}
		});
	}

	@Override
	public void insert(E object, final InsertOperationCallback<E> callback) {
		table.insert(object, new TableOperationCallback<E>() {

			@Override
			public void onCompleted(E object, Exception exception,
					ServiceFilterResponse arg2) {
				if (exception == null) {
					callback.onCompleteInsert(object);
				} else {
					operations.onException(exception,
							StorageOperation.EXTERNAL_INSERT);
				}
			}

		});

	}

	@Override
	public void delete(E object, final ChangeOperationCallback<E> callback) {
		table.delete(object, new TableDeleteCallback() {

			@Override
			public void onCompleted(Exception exception,
					ServiceFilterResponse arg1) {
				if (exception == null) {
					callback.onComplete();
				} else {
					operations.onException(exception,
							StorageOperation.EXTERNAL_DELETE);
				}

			}

		});

	}

	@Override
	public void update(E object, final ChangeOperationCallback<E> callback) {
		table.update(object, new TableOperationCallback<E>() {
			@Override
			public void onCompleted(E object, Exception exception,
					ServiceFilterResponse arg2) {
				if (exception == null) {
					callback.onComplete();
				} else {
					operations.onException(exception,
							StorageOperation.EXTERNAL_UPDATE);
				}

			}

		});
	}

}
