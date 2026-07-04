package com.flame.localize;

import java.util.Locale;

public interface IEnumeratedType<T> extends ILocalization {
    String getName();

    String getDisplay();

    String getDisplay(Locale locale);

    String getClassName();
}
