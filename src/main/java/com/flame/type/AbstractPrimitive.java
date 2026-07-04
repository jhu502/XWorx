package com.flame.type;

import com.flame.thing.IPropertyDefinition;
import com.flame.xui.WidgetMode;
import com.flame.xui.IWidget;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class AbstractPrimitive<T> implements IPrimitiveType<Object>, Serializable {
	private static final long serialVersionUID = 1L;
	protected T value;

	public AbstractPrimitive() {
	}

	public AbstractPrimitive(T value) {
		this.value = value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.OBJECT;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	@Override
	public String getEncode() {
		return this.getBaseType().name() + ":" + this.getBase64Encode(this.getValue().toString());
	}

	protected String getBase64Encode(String value) {
		return new String(Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}

	protected String getBase64Decode(String value) {
		return new String(Base64.getDecoder().decode((String) value), StandardCharsets.UTF_8);
	}

	@Override
	public IWidget getIWidget(WidgetMode model, IPropertyDefinition definition) {
		return null;
	}
}
