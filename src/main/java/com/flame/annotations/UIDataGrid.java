package com.flame.annotations;

import com.flame.xui.WidgetType;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@UIDefinition(component = WidgetType.DataGrid)
public @interface UIDataGrid {
	Class<?> type() default Object.class;

	String groupField() default "";
	
	String idField() default "";

	String title() default "";

	String toolbar() default "";

	String actionModel() default "";

	String contextMenu() default "";

	String sortName() default "";

	String sortOrder() default "";

	boolean fit() default false;

	boolean rowNumber() default true;

	boolean fitColumns() default false;

	boolean singleSelect() default true;

	boolean autoRowHeight() default false;

	boolean striped() default true;  //则把行条纹化。（即奇偶行使用不同背景色）

	boolean pagination() default true;

	boolean selectOnCheck() default false;

	boolean checkOnSelect() default false;

	int pageSize() default 100;

	int scrollbarSize() default 100;

	int rownumberWidth() default 20;

	boolean nowrap() default true;

	UIAction[] actions() default {};

	UIAction[] contexts() default {};

	UIColumn[] columns();
}
