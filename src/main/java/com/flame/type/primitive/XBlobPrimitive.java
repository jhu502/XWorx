package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XBlobPrimitive extends AbstractPrimitive<byte[]> {
	private static final long serialVersionUID = 1L;

	public XBlobPrimitive() {
		super();
	}

	public XBlobPrimitive(byte[] bytes) {
		this.setValue(bytes);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.BLOB;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
