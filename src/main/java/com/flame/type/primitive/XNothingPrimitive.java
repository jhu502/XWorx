package com.flame.type.primitive;

import java.io.Serializable;

import com.flame.type.IPrimitiveType;
import com.flame.type.XBaseType;

public class XNothingPrimitive implements IPrimitiveType<Object>, Serializable {
	private static final long serialVersionUID = 1L;
	
	public XNothingPrimitive() {
		super();
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public XBaseType getBaseType() {
		return XBaseType.NOTHING;
	}

	public String getEncode() {
		return "";
	}
}
