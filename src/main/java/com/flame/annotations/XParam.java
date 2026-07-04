package com.flame.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flame.type.XBaseType;

/**
 * Annotation for annotating service parameters as
 * JsonRpc params by name.
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XParam {
	
	/**
	 * @return the parameter's name.
	 */
	String name();
	
	XBaseType type();	//STRING / NUMBER / BOOLEAN / JSON / DATETIME / NOTHING
}
