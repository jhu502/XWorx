package com.flame.localize;

import java.util.Locale;

public interface ILocalization {
	String getEn_US();

	void setEn_US(String en_US);

	String getZh_CN();

	void setZh_CN(String zh_CN);

	String getDisplay();

	void setDisplay(String display);

	String getDisplay(Locale locale);

	default String getLocalDisplay() {
		Locale locale = LocalizationHelper.getLocale();
		String display = this.getDisplay(locale);
		if (this.isBlank(display)) {
			return this.getDisplay();
		} else {
			return display;
		}
	}

	default boolean isBlank(String value) {
		int len;
		if (value == null || (len = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < len; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}
}
