package com.thing.common;

import java.io.Closeable;

public interface IConnection<T extends Object> extends Closeable {
	
	public String getIdentity();
	
	public boolean startConnection(T entity);
}
