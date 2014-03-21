package com.ravolo.ies.merger;

import java.util.ArrayList;

public abstract class UndeletableMerger<E> extends DataMerger<E>{

	@Override
	protected ArrayList<E> getInternalToDelete(ArrayList<E> internalList,
			ArrayList<E> externalList) {
		//Undeletable
		return null;
	}

	@Override
	protected ArrayList<E> getExternalToDelete(ArrayList<E> internalList,
			ArrayList<E> externalList) {
		//undeletable
		return null;
	}
}
