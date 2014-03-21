package com.ravolo.ies.core;

import java.util.ArrayList;
import java.util.List;

import com.ravolo.ies.merger.DataMerger;
import com.ravolo.ies.storagerefresher.StorageRefresher;
import com.ravolo.ies.storages.AsyncStorage;
import com.ravolo.ies.storages.ChangeOperationCallback;
import com.ravolo.ies.storages.ImmediateStorage;
import com.ravolo.ies.storages.InsertOperationCallback;
import com.ravolo.ies.storages.QueryOperationCallback;
import com.ravolo.ies.storages.Storage;

/**
 * Base class of IES
 * 
 * @author Fuxing
 * 
 * @param <E>
 */
public abstract class InternalExternalStorage<E> extends Thread {
	private final boolean internalImmediate;
	private Storage<E> internalStorage;
	private AsyncStorage<E> externalStorage;
	protected StorageOperation<E> operations;

	protected ArrayList<E> internalDataList;
	protected ArrayList<E> externalDataList;

	private boolean internalLoaded;
	private boolean externalLoaded;
	private boolean networked;

	private DataMerger<E> dataMerger;

	public InternalExternalStorage(ImmediateStorage<E> internalStorage,
			AsyncStorage<E> externalStorage) {
		this.internalStorage = internalStorage;
		this.externalStorage = externalStorage;
		internalImmediate = true;
		networked = true;
	}

	public InternalExternalStorage(AsyncStorage<E> internalStorage,
			AsyncStorage<E> externalStorage) {
		this.internalStorage = internalStorage;
		this.externalStorage = externalStorage;
		internalImmediate = false;
		networked = true;
	}

	/**
	 * Start the operations
	 * 
	 * @param operation
	 * @param dataMerger
	 */
	public void start(StorageOperation<E> operation, DataMerger<E> dataMerger) {
		internalDataList = new ArrayList<E>();
		externalDataList = new ArrayList<E>();
		this.dataMerger = dataMerger;
		this.operations = operation;
		this.internalStorage.setOperations(operations);
		this.externalStorage.setOperations(operations);
		start();
	}

	/**
	 * Specifcy the network
	 * 
	 * @param operation
	 * @param dataMerger
	 * @param networked
	 */
	public void start(StorageOperation<E> operation, DataMerger<E> dataMerger,
			boolean networked) {
		this.networked = networked;
		start(operation, dataMerger);
	}

	@Deprecated
	public void start(StorageOperation<E> operation) {
		this.networked = false;
		start(operation, null);
	}

	@Override
	public void run() {
		initLoad();
	}

	void initLoad() {
		internalLoaded = false;
		externalLoaded = false;
		if (internalImmediate) {
			loadInternalDatabase();
			if (networked) {
				loadExternalDatabase();
			}
		} else {
			// If not immediate the load timing, sequence won't be same
		}
	}

	void loadInternalDatabase() {
		internalDataList.clear();
		if (internalImmediate) {
			ImmediateStorage<E> imStorage = (ImmediateStorage<E>) internalStorage;
			internalDataList
					.addAll(operations.onInternalLoad(imStorage.load()));
		}
		internalLoaded = true;
		operations.onCompleteInternalLoad(internalDataList);
		operations.ready();
		loadedInOrEx();
	}

	void loadExternalDatabase() {
		externalDataList.clear();
		externalStorage.load(new QueryOperationCallback<E>() {
			@Override
			public void onQueryComplete(List<E> dataList) {
				externalLoaded = true;
				externalDataList.addAll(operations.onExternalLoad(dataList));
				operations.onCompleteExternalLoad(externalDataList);
				loadedInOrEx();
			}

		});
	}

	void loadedInOrEx() {
		merge();
	}

	/**
	 * Only run this again if you declined this, because this is auto ran,
	 * return value is if it is ran it won;t ran if data aren't present
	 * 
	 * @return
	 */
	public boolean merge() {
		// should be threaded...!
		if (internalLoaded == true && externalLoaded == true) {
			if (operations.preMerge()) {
				dataMerger.start(internalDataList, externalDataList);
				startUpdateStorageData();
				return true;
			}
		}
		return false;
	}

	/**
	 * Refresh external
	 */
	public void refresh() {
		externalLoaded = false;
		// might need to spring off another thread
		loadExternalDatabase();
	}

	/**
	 * 
	 */
	public void refreshInternalExternal() {
		initLoad();
	}

	void startUpdateStorageData() {
		ArrayList<E> toUpdateExternalList = new ArrayList<E>();
		ArrayList<E> toUpdateInternalList = new ArrayList<E>();

		// Insert Section
		if (dataMerger.getExternalToInsert() != null) {
			toUpdateInternalList.addAll(externalStorage.insert(dataMerger
					.getExternalToInsert()));
		}
		if (dataMerger.getInternalToInsert() != null) {
			if (internalImmediate) {
				toUpdateExternalList
						.addAll(((ImmediateStorage<E>) internalStorage)
								.insert(dataMerger.getInternalToInsert()));
			} else {
				toUpdateExternalList.addAll(((AsyncStorage<E>) internalStorage)
						.insert(dataMerger.getInternalToInsert()));
			}
		}

		// Delete Section
		if (dataMerger.getExternalToDelete() != null) {
			externalStorage.delete(dataMerger.getExternalToDelete());
		}
		if (dataMerger.getInternalToDelete() != null) {
			if (internalImmediate) {
				((ImmediateStorage<E>) internalStorage).delete(dataMerger
						.getInternalToDelete());
			} else {
				((AsyncStorage<E>) internalStorage).delete(dataMerger
						.getInternalToDelete());
			}
		}

		// Update Section
		// Merging update dataM
		if (dataMerger.getExternalToUpdate() != null) {
			toUpdateExternalList.addAll(dataMerger.getExternalToUpdate());
		}
		if (dataMerger.getInternalToUpdate() != null) {
			toUpdateInternalList.addAll(dataMerger.getInternalToUpdate());
		}

		// Actual update
		externalStorage.update(toUpdateExternalList);
		if (internalImmediate) {
			((ImmediateStorage<E>) internalStorage)
					.update(toUpdateInternalList);
		} else {
			((AsyncStorage<E>) internalStorage).update(toUpdateInternalList);
		}
		// Run this on an external client to reread and load all.?

		// Remove existing data first
		internalDataList.clear();
		// Set immediate data
		if (internalImmediate) {
			internalDataList.addAll(((ImmediateStorage<E>) internalStorage)
					.load());
			operations.onMergeComplete(internalDataList);
		} else {
			((AsyncStorage<E>) internalStorage)
					.load(new QueryOperationCallback<E>() {
						@Override
						public void onQueryComplete(List<E> dataList) {
							internalDataList
									.addAll(((ImmediateStorage<E>) internalStorage)
											.load());
							operations.onMergeComplete(internalDataList);
						}
					});
		}
	}

	/**
	 * This will insert to both
	 */
	public void insert(final E object,
			final StorageOperation.Insert<E> insertOperation) {
		// TODO bad way, lots code
		if (externalLoaded) {
			externalStorage.insert(object, new InsertOperationCallback<E>() {
				@Override
				public void onCompleteInsert(E object) {
					if (internalImmediate) {
						insertOperation.onComplete(
								((ImmediateStorage<E>) internalStorage)
										.insert(object), true, true);
					} else {
						((AsyncStorage<E>) internalStorage).insert(object,
								new InsertOperationCallback<E>() {
									@Override
									public void onCompleteInsert(E object) {
										insertOperation.onComplete(object,
												true, true);
									}
								});
					}
				}
			});
		} else {
			if (internalImmediate) {

				insertOperation.onComplete(
						((ImmediateStorage<E>) internalStorage).insert(object),
						true, false);
			} else {
				((AsyncStorage<E>) internalStorage).insert(object,
						new InsertOperationCallback<E>() {
							@Override
							public void onCompleteInsert(E object) {
								insertOperation.onComplete(object, true, false);
							}
						});
			}
		}
	}

	/**
	 * Delete
	 * 
	 * @param object
	 * @param deleteOperation
	 */
	public void delete(final E object,
			final StorageOperation.Delete deleteOperation) {
		// TODO bad way, lots code
		if (externalLoaded) {
			externalStorage.delete(object, new ChangeOperationCallback<E>() {
				@Override
				public void onComplete() {
					if (internalImmediate) {
						((ImmediateStorage<E>) internalStorage).delete(object);
						deleteOperation.onComplete(true, true);
					} else {
						((AsyncStorage<E>) internalStorage).delete(object,
								new ChangeOperationCallback<E>() {
									@Override
									public void onComplete() {
										deleteOperation.onComplete(true, true);
									}
								});
					}
				}
			});
		} else {
			if (internalImmediate) {
				((ImmediateStorage<E>) internalStorage).delete(object);
				deleteOperation.onComplete(true, false);
			} else {
				((AsyncStorage<E>) internalStorage).delete(object,
						new ChangeOperationCallback<E>() {
							@Override
							public void onComplete() {
								deleteOperation.onComplete(true, false);
							}
						});
			}
		}
	}

	/**
	 * 
	 * @param object
	 * @param updateOperation
	 */
	public void update(final E object,
			final StorageOperation.Update updateOperation) {
		// TODO bad way, lots code
		if (externalLoaded) {
			externalStorage.delete(object, new ChangeOperationCallback<E>() {
				@Override
				public void onComplete() {
					if (internalImmediate) {
						((ImmediateStorage<E>) internalStorage).update(object);
						updateOperation.onComplete(true, true);
					} else {
						((AsyncStorage<E>) internalStorage).update(object,
								new ChangeOperationCallback<E>() {
									@Override
									public void onComplete() {
										updateOperation.onComplete(true, true);
									}
								});
					}
				}
			});
		} else {
			if (internalImmediate) {
				((ImmediateStorage<E>) internalStorage).update(object);
				updateOperation.onComplete(true, false);
			} else {
				((AsyncStorage<E>) internalStorage).update(object,
						new ChangeOperationCallback<E>() {
							@Override
							public void onComplete() {
								updateOperation.onComplete(true, false);
							}
						});
			}
		}
	}

	// Refresher module
	public StorageRefresher storageRefresher;

	/**
	 * 
	 * @param storageRefresher
	 */
	public void setStorageRefresher(StorageRefresher storageRefresher) {
		this.storageRefresher = storageRefresher;
		this.storageRefresher.setInternalExternalStorage(this);
		this.storageRefresher.start();
	}

	/**
	 * 
	 */
	public void stopStorageRefresher() {

	}

	/**
	 * 
	 * @return
	 */
	public boolean isInternalImmediate() {
		return internalImmediate;
	}

	/**
	 * 
	 * @return
	 */
	public Storage<E> getInternalStorage() {
		return internalStorage;
	}

	/**
	 * 
	 * @return
	 */
	public AsyncStorage<E> getExternalStorage() {
		return externalStorage;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<E> getMasterDataList() {
		return internalDataList;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<E> getLastestDataList() {
		return getMasterDataList();
	}

}
