package com.flame.xui;

import java.util.List;

public interface IGrid<T extends IRow<? extends ICell<T>>> {
    Object getProvider();

    String getName();

    String getDisplay();

    boolean isFieldSet();

    default boolean isAlignLabel() {
        return true;
    }

    List<T> getRows();
}
