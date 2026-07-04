package com.thing.common;

import com.thing.entity.ConnectableEntity;

public abstract class AbstractConnection<T extends ConnectableEntity> implements IConnection<T> {
	public boolean startConnection(T entity) {
		return true;
	}

	public String getIdentity() {
		return "";
	}
}
