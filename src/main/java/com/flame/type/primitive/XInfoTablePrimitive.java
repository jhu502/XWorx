package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.type.XInfoTable;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XInfoTablePrimitive extends AbstractPrimitive<XInfoTable> {
	private static final long serialVersionUID = 1L;

	public XInfoTablePrimitive() {
		super();
	}

	public XInfoTablePrimitive(XInfoTable value) {
		super(value);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.INFOTABLE;
	}

	@Override
	public XInfoTable getValue() {
		return this.value;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
