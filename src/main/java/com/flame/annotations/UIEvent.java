package com.flame.annotations;

public @interface UIEvent {
	String name() default "";

	String value() default "";
}
