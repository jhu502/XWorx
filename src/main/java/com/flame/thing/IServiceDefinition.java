package com.flame.thing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.orm.XPersistable;
import com.flame.type.XBaseType;

public interface IServiceDefinition extends XPersistable {

	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);

	public String getCode();

	public void setCode(String code);

	public XServiceType getServiceType();

	public void setServiceType(XServiceType serviceType);

	public List<Argument> getArguments();

	/**
	 * 操作集合时，只能望其集合对象中添加，不能替换集合对象，不然会报all-delete-orphan的错误
	 * 
	 * @param arguments
	 */
	public void setArguments(List<Argument> arguments);

	public void addArgument(Argument argument);

	public XBaseType getResultType();

	public void setResultType(XBaseType resultType);

	@JsonIgnore
	public IServiceProvider getServiceProvider();

	public void setServiceProvider(IServiceProvider serviceProvider);
}