package com.flame.annotations;

public @interface UIGrid {
	/**
	 * 设置当前Grid的对象类型，以便能够自动填值
	 * @return
	 */
    Class<?> provider() default Class.class;

    String title() default "";

    boolean fieldSet() default false;

    boolean alignLabel() default true;

    UIRow[] rows();
}
