package com.flame.xui;

import java.util.Arrays;
import java.util.List;

public interface ICell<T extends IRow<? extends ICell<T>>> {
    public static final String WIDGET = "Widget";
    public static final String TABLET = "Tablet";
    
    String getUiType();

    String getValue();

    void setValue(String value);

    String getStyle();

    void setStyle(String style);

    default List<IWidget> getWidgets() {
        return Arrays.asList();
    };

    void addWidget(IWidget widget);

    int getColspan();

    void setColspan(int colspan);

    int getRowCount();

    void setRowCount(int rowCount);
}
