package com.thing.common;

import com.flame.thing.IThingModel;
import com.thing.entity.IModeledEntity;

public class PlatformConfigEntity implements IModeledEntity {
	private static final long serialVersionUID = 1L;

	@Override
	public long getXid() {
		return 0;
	}

	@Override
	public String getPageUri() {
		return null;
	}

	@Override
	public IThingModel getThingModel() {
		return null;
	}

	@Override
	public void setThingModel(IThingModel thingModel) {
	}

	@Override
	public void setNumber(String number) {
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public void setDescription(String description) {
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getIconUI() {
		return null;
	}

	@Override
	public String getThingDisplay() {
		return null;
	}

	@Override
	public String getThingIdentity() {
		return null;
	}

}
