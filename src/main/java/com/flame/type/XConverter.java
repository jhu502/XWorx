package com.flame.type;

public interface XConverter<X, T> {
    public X converter(Object o);

    public T generator(Object o);
}
