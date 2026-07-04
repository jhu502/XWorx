package com.flame.annotations;

import com.flame.xui.WidgetType;

public @interface UIWidget {
	WidgetType type();

	String id() default "";

	String name() default "";

	String traits() default "";

	String style() default "";

	String url() default "";

	String text() default "";

	String precision() default "";

	UIEvent[] events() default {};
}
