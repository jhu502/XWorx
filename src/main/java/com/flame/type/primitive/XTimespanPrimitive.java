package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.type.XTimespan;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XTimespanPrimitive extends AbstractPrimitive<XTimespan> {
	private static final long serialVersionUID = 1L;

	public XTimespanPrimitive() {
		super();
	}

	public XTimespanPrimitive(XTimespan value) {
		super(value);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.TIMESPAN;
	}

	@Override
	public XTimespan getValue() {
		return this.value;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
