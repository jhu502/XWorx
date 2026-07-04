package com.thing;

import com.flame.config.basic.BasicConfiguration;
import com.flame.rpc.FlameMessage;
import com.flame.rpc.FlameResult;
import com.thing.common.IEndPoint;
import com.thing.runtime.IDispatchManager;
import com.thing.runtime.IEntityManager;
import com.thing.runtime.IThingManager;

public class ThingEntityHelper {
	public final static String PLATFORM_THING = "Flamethrower:Platform";
	private static IEntityManager service;
	private static IDispatchManager dispatch;
	private static IThingManager thing;

	public static IEntityManager service() {
		if (service == null) {
			service = BasicConfiguration.getBean(IEntityManager.class);
		}

		return service;
	}
	
	public static IDispatchManager dispatch() {
		if (dispatch == null) {
			dispatch = BasicConfiguration.getBean(IDispatchManager.class);
		}

		return dispatch;
	}
	
	public static IThingManager thing() {
		if (thing == null) {
			thing = BasicConfiguration.getBean(IThingManager.class);
		}

		return thing;
	}

	public static void requestFlameMessage(IEndPoint endpoint, FlameMessage message) {
		ThingEntityHelper.dispatch().requestFlameMessage(endpoint, message);
	}

	public static void responseFlameResult(IEndPoint endpoint, FlameResult result) {
		ThingEntityHelper.dispatch().responseFlameResult(endpoint, result);
	}
}
