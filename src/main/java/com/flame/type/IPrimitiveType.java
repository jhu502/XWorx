package com.flame.type;

import com.flame.thing.IPropertyDefinition;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;

import java.io.Serializable;

public interface IPrimitiveType<T> extends Serializable {
	T getValue();

	String getEncode();

	IBaseType getBaseType();

	/**
	 * 根据 WidgetMode 生成对应的 IWidget 组件。
	 *
	 * @param model 组件模型（Create/Edit/Display/Primary）
	 * @return 对应的 IWidget，默认返回 null
	 */
	default IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return null;
	}
}
