package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

import org.w3c.dom.Document;

public class XXMLPrimitive extends AbstractPrimitive<Document> {
	private static final long serialVersionUID = 1L;

	public XXMLPrimitive() {
		super();
	}

	public XXMLPrimitive(Document value) {
		super(value);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.XML;
	}

	@Override
	public Document getValue() {
		return this.value;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
