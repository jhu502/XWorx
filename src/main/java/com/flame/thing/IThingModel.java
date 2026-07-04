package com.flame.thing;

import java.lang.reflect.Field;
import java.util.Map;

import com.flame.orm.XPersistable;
import com.flame.localize.ILocalization;

public interface IThingModel extends IPropertyProvider, IServiceProvider, ILocalization, XPersistable, Comparable<ILocalization> {
	String getNumber();

	void setNumber(String number);

	String getName();

	void setName(String name);

	String getIcon();

	void setIcon(String icon);

	void setDescription(String description);

	String getPageUri();

	void setPageUri(String pageUri);

	void setEntity(String entity);

	Class<?> getEntityClass();

	String getModel();

	void setModel(String model);

	IThingModel getThingModel();

	void setThingModel(IThingModel model);

	Class<?> getModelClass();

	String getModelKey();

	Map<String, Field> getNativeFields();

	IServiceDefinition getServiceDefinition(String serviceName);

	@Override
	default String getDisplay() {
		return this.getOid();
	}
}
