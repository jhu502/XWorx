package com.flame.xui;

public interface IWidget extends IComponent {
	String renderHTML();
	
	String getId();
    
    default String getName() {
        return "";
    }
	
	default void setId(String id) {}

	default void setStyle(String style) {};

	default void setEasyUI(boolean bool) {}

	/**
	 * 标识当前Widget是否是Easyui组件, Widget被嵌入到DataGrid/TreeGrid/PropertyGrid的数据
	 * 行时,easyui组件无法被渲染, 只适合作为普通的html来处理
	 * @return
	 */
	default boolean isEasyUI() {
		return false;
	}

	/**
	 * GuiWidget中自动为Widget组件赋值
	 *
	 * @param object
	 */
	void inflate(Object object);
    
    void setWidgetMode(WidgetMode model);
}
