package com.flame.thing;

import com.flame.config.FlameConfiguration;

public class ThingModelHelper {
	private static IThingModelManager manager;

	private ThingModelHelper() {
	}

	public static IThingModelManager manager() {
		if (manager == null) {
			manager = FlameConfiguration.getBean(IThingModelManager.class);
		}

		return manager;
	}
}
