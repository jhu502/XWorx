package com.flame.orm;

import com.flame.type.XBaseType;

public class XAttribute extends XJsonType<XAttribute> {
    private static final long serialVersionUID = 1L;
    private String name;
    private XBaseType type;
    private Object value;

    public static XAttribute newInstance(XBaseType type, String name, String value) {
        XAttribute xvalue = new XAttribute();
        xvalue.setName(name);
        xvalue.setType(type);
        xvalue.setValue(value);

        return xvalue;
    }

    public XBaseType getType() {
        return type;
    }

    public void setType(XBaseType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
