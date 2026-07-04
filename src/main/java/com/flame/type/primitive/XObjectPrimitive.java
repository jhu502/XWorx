package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XObjectPrimitive extends AbstractPrimitive<Object> {
	private static final long serialVersionUID = 1L;
	private XBaseType baseType;

	public XObjectPrimitive() {
		super();
	}

	public XObjectPrimitive(Object value, XBaseType baseType) {
		super(value);
		this.baseType = baseType;
	}

	public void setBaseType(XBaseType baseType) {
		this.baseType = baseType;
	}

	@Override
	public XBaseType getBaseType() {
		return this.baseType;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
