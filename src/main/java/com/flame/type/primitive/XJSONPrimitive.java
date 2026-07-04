package com.flame.type.primitive;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.AbstractPrimitive;
import com.flame.type.XBaseType;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;
import com.flame.xui.service.UIComponentService;

import org.json.JSONObject;

public class XJSONPrimitive extends AbstractPrimitive<JSONObject> {
	private static final long serialVersionUID = 1L;

	public XJSONPrimitive() {
		super(new JSONObject());
	}

	public XJSONPrimitive(JSONObject value) {
		super(value);
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.JSON;
	}

	@Override
	public JSONObject getValue() {
		return this.value;
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return UIComponentService.createWidget(definition, model);
	}
}
