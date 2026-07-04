package com.flame.annotations;

public @interface UIRow {
	public String groupName() default "";

	public UICell[] cells();
}
