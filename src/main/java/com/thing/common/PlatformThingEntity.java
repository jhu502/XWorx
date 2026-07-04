package com.thing.common;

import com.flame.thing.IModelManaged;
import com.thing.ThingEntityHelper;

public class PlatformThingEntity extends AbstractThingModel<PlatformConfigEntity> {
	public PlatformThingEntity() {
		super(new PlatformConfigEntity());
	}
	
	public IThingManaged<IModelManaged> binding(String identity, IEndPoint endpoint) {
		return ThingEntityHelper.dispatch().binding(identity, endpoint);
	}

	public void unbind(String identity) {
	}
}
