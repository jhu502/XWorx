package com.flame.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XDefinition {
	String name();
	
	Class<?> config();
	
	String icon();

	String display();
	
	String description() default "";
	
	String pageUri() default "";

	String en_US() default "";

	String zh_CN() default "";
}
