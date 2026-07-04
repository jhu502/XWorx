package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

public class XPasswordPrimitive extends AbstractPrimitive<String> {
	private static final long serialVersionUID = 1L;

	public XPasswordPrimitive() {
		super();
	}

	public XPasswordPrimitive(String value) {
		this(value, false);
	}

	XPasswordPrimitive(String value, boolean valueIsEncrypted) {
		this.value = "";
		if (valueIsEncrypted) {
			this.value = value;
		} else {
			this.setValue(value);
		}
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
