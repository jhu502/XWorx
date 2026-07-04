package com.flame.annotations;

public @interface UIOption {
	public String key();
	
	public String value() default "";
}
