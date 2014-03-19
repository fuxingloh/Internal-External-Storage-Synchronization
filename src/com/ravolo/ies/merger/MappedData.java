package com.ravolo.ies.merger;

public class MappedData<E> {
	public final E internalData;
	public final E externalData;

	public static final int DO_NOTHING = 0;
	public static final int UPDATE_EXTERNAl_TO_INTERNAL = 1;
	public static final int UPDATE_INTERNAL_TO_EXTERNAL = 2;
	public int doWhat = DO_NOTHING;

	public MappedData(E internalData, E externalData) {
		this.internalData = internalData;
		this.externalData = externalData;

	}

	public int getDoWhat() {
		return doWhat;
	}

	public void setDoWhat(int doWhat) {
		this.doWhat = doWhat;
	}

	public E getInternalData() {
		return internalData;
	}

	public E getExternalData() {
		return externalData;
	}
	
	public void updateInternalToExternal(){
		setDoWhat(UPDATE_INTERNAL_TO_EXTERNAL);
	}
	
	public void updateExternalToInternal(){
		setDoWhat(UPDATE_EXTERNAl_TO_INTERNAL);
	}

}
