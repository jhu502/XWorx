package com.flame.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flame.type.ServiceType;
import com.flame.type.XBaseType;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XService {

	ServiceType serviceType();

	XBaseType resultType();

	String name();

	XParam[] params() default {};
}
