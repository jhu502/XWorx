package com.flame.action;

import com.flame.config.basic.BasicConfiguration;

public class ActionHelper {
	private static IActionManager manager;

	private ActionHelper() {
	}

	public static IActionManager manager() {
		if (manager == null) {
			manager = BasicConfiguration.getBean(IActionManager.class);
		}

		return manager;
	}
}
