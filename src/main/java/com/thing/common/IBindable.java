package com.thing.common;

import com.flame.thing.XBindType;

public interface IBindable {
	public XBindType getBindType();

	public void setBindType(XBindType btype);
	
	public boolean bind(IEndPoint endpoint);
	
	public boolean unbind(IEndPoint connection);
	
	public boolean isBinding();
	
	public IEndPoint getEndPoint();
	
	public String getThingIdentity();
}
