package com.ravolo.ies.core;

import java.util.List;

public abstract class StorageOperation<E> {
	/**
	 * Fresh data from internal system, make changes to this if you want to
	 * change things
	 * 
	 * @param dataList
	 * @return
	 */
	public List<E> onInternalLoad(List<E> dataList) {
		return dataList;
	}

	public abstract void onCompleteInternalLoad(List<E> dataList);

	public List<E> onExternalLoad(List<E> dataList) {
		return dataList;
	}

	public abstract void onCompleteExternalLoad(List<E> dataList);

	public void ready() {

	}

	public boolean shouldMerge() {
		return true;
	}

	public boolean preMerge() {
		return shouldMerge();
	}

	public abstract void onMergeComplete(List<E> dataList);

	public List<E> preInternalInsert(List<E> dataList) {
		return dataList;
	}

	public List<E> preExternalInsert(List<E> dataList) {
		return dataList;
	}

	public List<E> preInternalUpdate(List<E> dataList) {
		return dataList;
	}

	public List<E> preExternalUpdate(List<E> dataList) {
		return dataList;
	}

	public List<E> preInternalDelete(List<E> dataList) {
		return dataList;
	}

	public List<E> preExternalDelete(List<E> dataList) {
		return dataList;
	}

	public static final int INTERNAL_LOAD = 0;
	public static final int INTERNAL_LOAD_COMPLETE = 1;
	public static final int INTERNAL_INSERT = 2;
	public static final int INTERNAL_INSERT_COMPLETE = 3;
	public static final int INTERNAL_UPDATE = 4;
	public static final int INTERNAL_UPDATE_COMPLETE = 5;
	public static final int INTERNAL_DELETE = 6;
	public static final int INTERNAL_DELETE_COMPLETE = 7;

	public static final int EXTERNAL_LOAD = 10;
	public static final int EXTERNAL_LOAD_COMPLETE = 11;
	public static final int EXTERNAL_INSERT = 12;
	public static final int EXTERNAL_INSERT_COMPLETE = 13;
	public static final int EXTERNAL_UPDATE = 14;
	public static final int EXTERNAL_UPDATE_COMPLETE = 15;
	public static final int EXTERNAL_DELETE = 16;
	public static final int EXTERNAL_DELETE_COMPLETE = 17;

	public abstract void onException(Exception exception, int where);

	public interface StorageInterface {
		/**
		 * Exception caught on main StorageOperation
		 * 
		 * @author Fuxing
		 * 
		 */
		public interface DeleteOperation {
			public void onComplete(boolean internalSucceed,
					boolean externalSucceed);
		}

		/**
		 * Exception caught on main StorageOperation
		 * 
		 * @author Fuxing
		 * 
		 */
		public interface InsertOperation<E> {
			public void onComplete(E object, boolean internalSucceed,
					boolean externalSucceed);
		}

		/**
		 * Exception caught on main StorageOperation
		 * 
		 * @author Fuxing
		 * 
		 */
		public interface UpdateOperation {
			public void onComplete(boolean internalSucceed,
					boolean externalSucceed);

		}
	}
}
