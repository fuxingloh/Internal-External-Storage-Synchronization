package com.ravolo.ies.merger;

import java.util.ArrayList;

import com.ravolo.ies.exception.AlreadyStartedException;

public abstract class DataMerger<E> {
	protected ArrayList<E> internalList;// Clean
	protected ArrayList<E> externalList;// Clean

	protected ArrayList<E> internalToInsert; // done
	protected ArrayList<E> externalToInsert;// done

	protected ArrayList<E> internalToDelete;
	protected ArrayList<E> externalToDelete;

	protected ArrayList<E> internalToUpdate;
	protected ArrayList<E> externalToUpdate;

	protected ArrayList<MappedData<E>> mappedList;
	private boolean started = false;

	public void start(ArrayList<E> internalList, ArrayList<E> externalList) {
		this.internalList = internalList;
		this.externalList = externalList;
		startProcess();
	}

	protected void startProcess() {
		started = true;
		mappedList = new ArrayList<MappedData<E>>();
		internalToInsert = getInternalToInsert(internalList, externalList);
		externalToInsert = getExternalToInsert(internalList, externalList);
		internalToDelete = getInternalToDelete(internalList, externalList);
		externalToDelete = getExternalToDelete(internalList, externalList);
		lookThroughtMappedData();
		pushTodoInMappedData();
	}

	protected ArrayList<E> getInternalToInsert(ArrayList<E> internalList,
			ArrayList<E> externalList) {
		ArrayList<E> internalToInsert = new ArrayList<E>();
		;
		for (E exElement : externalList) {
			String uExId = getExternalId(exElement);
			for (E inElement : internalList) {
				if (uExId.equals(getExternalId(inElement))) {
					mappedList.add(new MappedData<E>(inElement, exElement));
					break;
				}
				internalToInsert.add(exElement);
			}
		}
		return internalToInsert;
	}

	protected ArrayList<E> getExternalToInsert(ArrayList<E> internalList,
			ArrayList<E> externalList) {
		ArrayList<E> externalToInsert = new ArrayList<E>();
		;
		for (E inElement : internalList) {
			if (getExternalId(inElement) == null
					|| getExternalId(inElement).equals("")) {
				externalList.add(inElement);
			}
		}
		return externalToInsert;
	}

	/**
	 * Delete can be ignored if you are using bolean to check deleted
	 * 
	 * @param internalList
	 * @param externalList
	 * @return
	 */
	protected abstract ArrayList<E> getInternalToDelete(
			ArrayList<E> internalList, ArrayList<E> externalList);

	/**
	 * Delete can be ignored if you are using bolean to check deleted
	 * 
	 * @param internalList
	 * @param externalList
	 * @return
	 */
	protected abstract ArrayList<E> getExternalToDelete(
			ArrayList<E> internalList, ArrayList<E> externalList);

	protected abstract String getExternalId(E element);

	protected void lookThroughtMappedData() {
		for (MappedData<E> data : mappedList) {
			doWhatWithMappedData(data);
		}
	}

	protected abstract void doWhatWithMappedData(MappedData<E> data);

	void pushTodoInMappedData() {
		internalToUpdate = new ArrayList<E>();
		externalToUpdate = new ArrayList<E>();
		for (MappedData<E> data : mappedList) {
			switch (data.getDoWhat()) {
			case MappedData.UPDATE_EXTERNAl_TO_INTERNAL:
				internalToUpdate.add(data.getExternalData());
				break;
			case MappedData.UPDATE_INTERNAL_TO_EXTERNAL:
				externalToUpdate.add(data.getInternalData());
				break;
			}
		}
	}

	// Setter
	/**
	 * Checking editable, internal usage only
	 */
	void checkEditable() {
		if (started) {
			throw new AlreadyStartedException();
		}
	}

	public ArrayList<E> getInternalToInsert() {
		return internalToInsert;
	}

	public ArrayList<E> getExternalToInsert() {
		return externalToInsert;
	}

	public ArrayList<E> getInternalToDelete() {
		return internalToDelete;
	}

	public ArrayList<E> getExternalToDelete() {
		return externalToDelete;
	}

	public ArrayList<E> getInternalToUpdate() {
		return internalToUpdate;
	}

	public ArrayList<E> getExternalToUpdate() {
		return externalToUpdate;
	}

	//Getter
	
}
