package com.thing.runtime;

import org.graalvm.polyglot.Value;

import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.thing.common.IThingManaged;
import com.flame.type.XBaseType;

public interface IScriptModel {
	public Value getNewInstance(IThingManaged<?> entity);

	public boolean hasFunction(String funcName);

	public boolean hasProperty(String propName);
	
	public ScriptFunction getScriptFunction(String serviceName) throws UnknownIdentifierException;

	public XBaseType getPropertyType(String propertyName) throws UnknownIdentifierException;

	public String getDefaultValue(String propertyName) throws UnknownIdentifierException;

	public boolean isNullable(String propertyName) throws UnknownIdentifierException;

	public boolean isPersistent(String propertyName) throws UnknownIdentifierException;

	public boolean isReadOnly(String propertyName) throws UnknownIdentifierException;

	public boolean isLogged(String propertyName) throws UnknownIdentifierException;
}
