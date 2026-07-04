package com.thing.entity;

import com.flame.orm.XPersistable;
import com.flame.thing.IModelManaged;
import com.flame.thing.IThingModel;

public interface IModeledEntity extends IModelManaged, XPersistable {

	String getPageUri();

	IThingModel getThingModel();

	void setThingModel(IThingModel thingModel);
	
	void setNumber(String number);
	
	void setName(String name);
	
	void setDescription(String description);

	String getIcon();

	String getIconUI();
	
	String getThingDisplay();

	String getThingIdentity();
}
