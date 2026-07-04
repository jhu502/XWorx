package com.flame.xui;

import java.util.List;

public interface IRow<T extends ICell<? extends IRow<T>>> {
    List<T> getCells();

    T getCell(int i);

    T addCell(T cell);

    T setCell(int i, T cell);

    int size();
}
