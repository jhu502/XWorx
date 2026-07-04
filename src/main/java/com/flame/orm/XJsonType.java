package com.flame.orm;

import java.io.Serializable;

public class XJsonType<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String xclass;

    public XJsonType() {
        this.xclass = this.getClass().getName();
    }

    public String getXclass() {
        return this.xclass;
    }

    public void postProcess() {}
}
