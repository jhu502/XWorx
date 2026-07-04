package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XGuidPrimitive extends AbstractPrimitive<String> {
	private static final long serialVersionUID = 1L;

	public XGuidPrimitive() {
		super();
	}

	public XGuidPrimitive(String value) {
		super(value);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.GUID;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
