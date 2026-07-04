package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.type.XLocation;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XLocationPrimitive extends AbstractPrimitive<XLocation> {
	private static final long serialVersionUID = 1L;

	public XLocationPrimitive() {
		super();
	}

	public XLocationPrimitive(XLocation value) {
		super(value);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.LOCATION;
	}

	@Override
	public XLocation getValue() {
		return this.value;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
