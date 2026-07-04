package com.flame.localize;

import java.util.List;
import java.util.Locale;

import com.flame.config.JPAConfiguration;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.PropertyUtil;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEnumerated<T extends AbstractEnumerated<T>> extends XObject implements IEnumeratedType<T> {
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "name", unique = true, nullable = false)
    private String name = "";
    @Basic
    @Column(name = "display")
    private String display;
    @Basic
    @Column(name = "en_US")
    private String en_US = "";
    @Basic
    @Column(name = "zh_CN")
    private String zh_CN = "";
    @Basic
    @Column(name = "description", length = 1000)
    private String description;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String getDisplay() {
        if (this.display == null)
            return this.getName();
        else
            return this.display;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDisplay(Locale locale) {
        if (locale == null)
            return this.getDisplay();

        String property = locale.getLanguage() + "_" + locale.getCountry();
        String value = (String) PropertyUtil.getProperty(this, property);
        if (isBlank(value)) {
            return this.getDisplay();
        } else {
            return value;
        }
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }
    
    public static <T extends IEnumeratedType<T>> List<T> toRBTypeList(Class<T> clazz) {
        return PersistenceHelper.service().query(clazz, new Object[0][0]);
    }

    public static <T extends IEnumeratedType<T>> List<T> toRBType(Class<T> clazz, String field, String value) {
        return JPAConfiguration.instance().getEnumeratedType(clazz, field, value);
    }
}
