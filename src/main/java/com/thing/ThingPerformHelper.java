package com.thing;

import com.flame.config.basic.BasicConfiguration;
import com.thing.runtime.ThingPerformService;

public class ThingPerformHelper {
	private static ThingPerformService service;
	
	public static ThingPerformService service() {
		if (service == null) {
			service = BasicConfiguration.getBean(ThingPerformService.class);
		}
		
		return service;
	}

}
