package com.ravolo.ies.storagerefresher;

public class ConstantRefresher extends StorageRefresher {

	public ConstantRefresher(int timer) {
		super(timer);
	}

	@Override
	protected boolean shouldUpdate() {
		return true;

	}

}
