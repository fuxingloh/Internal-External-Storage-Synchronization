package com.ravolo.ies.core;

import java.util.List;

public abstract class SimpleOperation<E> extends StorageOperation<E> {
	// Flags
	public static final int INTERNAL = 1;
	public static final int MERGED = 2;

	@Override
	public void onCompleteInternalLoad(List<E> dataList) {
		onDataUpdated(dataList, INTERNAL);
	}

	@Override
	public void onCompleteExternalLoad(List<E> dataList) {
		// Do nothing here
	}

	@Override
	public void onMergeComplete(List<E> dataList) {
		onDataUpdated(dataList, MERGED);
	}

	public abstract void onDataUpdated(List<E> dataList, int where);

}
