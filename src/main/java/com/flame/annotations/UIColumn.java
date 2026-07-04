package com.flame.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface UIColumn {
	String field();

	String title() default "";

	String align() default "left";

	String width() default "";
	
	String order() default "";
	
	String formatter() default "";
	
	boolean checkbox() default false;
	
	boolean hidden() default false;

	boolean sortable() default false;
	
	boolean frozen() default false;
	
	boolean expander() default false;
	
	UIWidget[] widget() default {};
}
