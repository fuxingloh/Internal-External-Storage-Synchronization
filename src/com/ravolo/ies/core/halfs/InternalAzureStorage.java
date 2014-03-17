package com.ravolo.ies.core.halfs;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.ravolo.ies.core.InternalExternalStorage;
import com.ravolo.ies.storages.AsyncStorage;
import com.ravolo.ies.storages.ImmediateStorage;
import com.ravolo.ies.storages.external.AzureMobileServiceStorage;

public class InternalAzureStorage<E> extends InternalExternalStorage<E> {

	public InternalAzureStorage(ImmediateStorage<E> internalStorage,
			Class<E> clazz, int dataVersion, MobileServiceClient client) {
		super(internalStorage, new AzureMobileServiceStorage<E>(clazz,
				dataVersion, client));

	}

	public InternalAzureStorage(AsyncStorage<E> internalStorage,
			Class<E> clazz, int dataVersion, MobileServiceClient client) {
		super(internalStorage, new AzureMobileServiceStorage<E>(clazz,
				dataVersion, client));
	}

}
