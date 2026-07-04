package com.thing.worx;

import org.springframework.stereotype.Component;

import com.flame.config.basic.BasicConfiguration;

@Component
public class ThingworxHelper {
    private static IThingworx twProxy = null;

    public static void setIThingworx(IThingworx thingworx) {
        twProxy = thingworx;
        BasicConfiguration.regSingletonBean("XWorxSubSystem", twProxy);
    }

    public static IThingworx thingworx() {
        return twProxy;
    }
}
