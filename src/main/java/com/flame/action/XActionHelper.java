package com.flame.action;

import com.flame.config.FlameConfiguration;

public class XActionHelper {
	private static IActionManager manager;

	private XActionHelper() {
	}

	public static IActionManager manager() {
		if (manager == null) {
			manager = FlameConfiguration.getBean(IActionManager.class);
		}

		return manager;
	}
}
