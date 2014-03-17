package com.ravolo.ies.core;

import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.ravolo.ies.core.halfs.InternalAzureStorage;
import com.ravolo.ies.storages.internal.SqliteStorage;

public class SqliteAzureStorage<E> extends InternalAzureStorage<E> {

	public SqliteAzureStorage(Class<E> clazz, Context context, int dataVersion,
			MobileServiceClient client) {
		super(new SqliteStorage<E>(clazz, dataVersion, context), clazz,
				dataVersion, client);
	}

}
