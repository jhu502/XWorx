package com.flame.annotations;

import com.flame.xui.WidgetType;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@UIDefinition(component = WidgetType.MeshGrid)
public @interface UIMeshGrid {
	UIGrid[] grids() default {};
}
