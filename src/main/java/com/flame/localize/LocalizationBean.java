package com.flame.localize;

import com.flame.util.PropertyUtil;

import java.util.Locale;

public class LocalizationBean implements ILocalization {
    private String display;
    private String en_US;
    private String zh_CN;

    @Override
    public String getDisplay() {
        return this.display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String getEn_US() {
        return this.en_US;
    }

    @Override
    public void setEn_US(String en_US) {
        this.en_US = en_US;
    }

    @Override
    public String getZh_CN() {
        return this.zh_CN;
    }

    @Override
    public void setZh_CN(String zh_CN) {
        this.zh_CN = zh_CN;
    }

    @Override
    public String getDisplay(Locale locale) {
        if (locale == null)
            return this.getDisplay();

        String property = locale.getLanguage() + "_" + locale.getCountry();
        return (String) PropertyUtil.getProperty(this, property);
    }
}
