package com.flame.annotations;

import com.flame.xui.WidgetType;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@UIDefinition(component = WidgetType.TreeGrid)
public @interface UITreeGrid {
	Class<?> type() default Object.class;

	String idField() default "";

	String title() default "";

	String treeField();

	String toolbar() default "";

	String actionModel() default "";

	String contextMenu() default "";

	UIAction[] actions() default {};

	UIAction[] contexts() default {};
	
	String sortName() default "";
	
	String sortOrder() default "";

	boolean fit() default false;
	
	boolean animate() default true;

	boolean rowNumber() default true;

	boolean fitColumns() default false;

	boolean singleSelect() default true;
	
	boolean autoRowHeight() default false;
	
	boolean striped() default true;  //则把行条纹化。（即奇偶行使用不同背景色）
	
	boolean pagination() default false;
	
	boolean selectOnCheck() default false;
	
	boolean checkOnSelect() default false;
	
	int pageSize() default 100;
	
	int scrollbarSize() default 100;
	
	int rownumberWidth() default 20;

	UIColumn[] columns();
}
