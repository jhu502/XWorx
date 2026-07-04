package com.flame.thing;

import com.flame.orm.XPersistable;

public interface IModelManaged extends XPersistable {
	public IThingModel getThingModel();
	
	public void setThingModel(IThingModel thingModel);
	
	public String getIcon();
	
	public String getThingIdentity();
}
