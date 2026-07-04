package com.flame.annotations;

import com.flame.xui.WinType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface UIAction {
    String name();

    String display() default "";

    String icon() default "";

    String style() default "";

    String url() default "";

    String processor() default "";

    WinType winType();

    String beforeJS() default "";

    String afterJS() default "";
}
