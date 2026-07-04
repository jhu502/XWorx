package com.thing.common;

public interface IConnectable<T extends IConnection<?>> {
	public T getConnection();

	public void setConnection(T endPoint);
	
	public boolean isConnected();
}
