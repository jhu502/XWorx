package com.thing.runtime;

import com.flame.thing.IPropertyDefinition;
import com.flame.type.XBaseType;

public class ScriptProperty {
    XBaseType propertyType;
    String propName;
    String defaultValue;
    boolean nullable;
    boolean persistent;
    boolean readOnly;
    boolean logged;

    ScriptProperty(IPropertyDefinition propertyDef) {
        this.propName = propertyDef.getName();
        this.defaultValue = propertyDef.getDefaultValue();
        this.nullable = propertyDef.isNullable();
        this.persistent = propertyDef.isPersistent();
        this.readOnly = propertyDef.isReadOnly();
        this.logged = propertyDef.isLogged();
    }
}
