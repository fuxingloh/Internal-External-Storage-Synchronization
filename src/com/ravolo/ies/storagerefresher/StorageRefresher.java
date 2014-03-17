package com.ravolo.ies.storagerefresher;

import com.ravolo.ies.core.InternalExternalStorage;

public abstract class StorageRefresher extends Thread{
	private int timer;
	private boolean stop;
	private InternalExternalStorage<?> internalExternalStorage;
	public StorageRefresher(int timer){
		this.timer = timer;
		this.stop = false;
	}
	
	@Override
	public void run(){
		
	}
	
	public void checkUpdate(){
		if(stop){
			return;
		}
		if(shouldUpdate()){
			internalExternalStorage.refresh();
		}
		checkAgain();
	}
	
	void checkAgain(){
		try {
			this.wait(timer);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		checkUpdate();
	}
	protected abstract boolean shouldUpdate();
	
	public void stopRefresh(){
		stop = true;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public void setInternalExternalStorage(
			InternalExternalStorage<?> internalExternalStorage) {
		this.internalExternalStorage = internalExternalStorage;
	}
	
}
