package com.flame.thing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.orm.XPersistable;
import com.flame.localize.ILocalization;

public interface IPropertyLayout extends ILocalization, Comparable<ILocalization>, XPersistable {

    String getName();

    void setName(String name);

    LayoutType getLayoutType();

    void setLayoutType(LayoutType layoutType);

    @JsonIgnore
    IPropertyProvider getPropertyProvider();

    void setPropertyProvider(IPropertyProvider propertyProvider);

	default String getDisplay() {
		return this.getOid();
	}
}
