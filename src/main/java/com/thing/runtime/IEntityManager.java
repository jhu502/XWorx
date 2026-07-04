package com.thing.runtime;

import java.util.List;
import java.util.Map;

import com.flame.thing.IModelManaged;
import com.flame.thing.IThingModel;
import com.thing.entity.IModeledEntity;

public interface IEntityManager {
	
	public List<?> queryConfigEntity(IThingModel model, String field, String value);

	public IModelManaged findConfigEntity(String thingIdentity);
	
	public IModelManaged createConfigEntity(IThingModel thgModel, Map<String, String> params);
	
	public IModelManaged updateConfigEntity(IModeledEntity entityThing, Map<String, String> params);
}
