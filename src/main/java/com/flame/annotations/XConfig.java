package com.flame.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flame.type.XBaseType;

@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XConfig {
	String name();
	
	XBaseType baseType();

	String friendlyName();
	
	String description();
	
	String defaultValue() default "";
	
	boolean required() default false;
	
	boolean created() default true;
	
	boolean modified() default true;
}
