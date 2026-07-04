package com.thing.common;

import com.oracle.truffle.api.interop.UnknownIdentifierException;

public interface IThingManaged<S> {

	public long getId();

	public String getThingIdentity();

	public S getThingEntity();

	public void setThingEntity(S target);

	public void startThing();

	public void stopThing();

	public Object invokeService(String methodName, Object... arguments) throws Exception;

	public Object invokeMember(String methodName, Object... arguments);

	public boolean hasProperty(String propName);

	public Object readProperty(String propName) throws UnknownIdentifierException;

	public void writeProperty(String propName, Object value) throws UnknownIdentifierException;
}
