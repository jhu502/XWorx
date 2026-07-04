package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XImagePrimitive extends AbstractPrimitive<byte[]> {
	private static final long serialVersionUID = 1L;

	public XImagePrimitive() {
		super(new byte[0]);
	}

	public XImagePrimitive(byte[] value) {
		super(value);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.IMAGE;
	}

	@Override
	public byte[] getValue() {
		return this.value;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
