package com.flame.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface UICell {
	String label() default "";

	int colspan() default 0;

	int rowCount() default 1;

	String style() default "";

	boolean required() default false;

	UIWidget[] widget() default {};
}
