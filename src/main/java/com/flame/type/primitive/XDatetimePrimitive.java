package com.flame.type.primitive;

import java.util.Date;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XDatetimePrimitive extends AbstractPrimitive<Date> {
	private static final long serialVersionUID = 1L;

	public XDatetimePrimitive() {
		super();
	}

	public XDatetimePrimitive(Date value) {
		super(value);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.DATETIME;
	}

	@Override
	public Date getValue() {
		return this.value;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
