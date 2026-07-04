package com.thing.runtime;

import com.flame.rpc.FlameMessage;
import com.flame.rpc.FlameResult;
import com.thing.common.IEndPoint;
import com.thing.common.IThingManaged;

public interface IThingManager {
	public IThingManaged<?> getThing(String identity);
	
	public IThingManaged<?> startThing(String identity);
	
	public IThingManaged<?> stopThing(String identity);
	
	public FlameResult dispatchService(FlameMessage message, IEndPoint endpoint);
}
