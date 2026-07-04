package com.flame.localize;

import java.util.Locale;

import com.flame.util.PropertyUtil;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.springframework.context.i18n.LocaleContextHolder;

import com.flame.orm.XObject;
import com.flame.util.PinYinUtils;

@MappedSuperclass
public class AbstractLocalization extends XObject implements ILocalization, Comparable<ILocalization> {
	private static final long serialVersionUID = 1L;
	@Basic
    @Column(name = "display")
    private String display = "";
    @Basic
    @Column(name = "en_US")
    private String en_US = "";
    @Basic
    @Column(name = "zh_CN")
    private String zh_CN = "";

    public String getEn_US() {
        return en_US;
    }

    public void setEn_US(String en_US) {
        this.en_US = en_US;
    }

    public String getZh_CN() {
        return zh_CN;
    }

    public void setZh_CN(String zh_CN) {
        this.zh_CN = zh_CN;
    }

    public String getDisplay() {
        return this.display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDisplay(Locale locale) {
        if (locale == null)
            return this.display;

        String property = locale.getLanguage() + "_" + locale.getCountry();
        Object value = PropertyUtil.getProperty(this, property);
        if (value == null) {
            return this.display;
        } else {
            return (String) value;
        }
    }

    @Override
    public int compareTo(ILocalization o) {
        Locale locale = LocaleContextHolder.getLocale();
        locale = locale == null ? Locale.ENGLISH : locale;
        String display = this.getDisplay(locale);
        String _display = o.getDisplay(locale);
        String letter = PinYinUtils.getFirstLetter(display);
        String _letter = PinYinUtils.getFirstLetter(_display);

        return letter.compareTo(_letter);
    }
}
