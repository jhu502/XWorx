package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XNumberPrimitive extends AbstractPrimitive<Double> {
	private static final long serialVersionUID = 1L;

	public XNumberPrimitive() {
		super();
	}

	public XNumberPrimitive(double value) {
		super(value);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.NUMBER;
	}

	@Override
	public Double getValue() {
		return this.value;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
