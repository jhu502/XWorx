package com.flame.orm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import org.hibernate.annotations.ColumnTransformer;

import com.flame.util.XException;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity extends XObject {
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "description", length = 1000)
    private String description = "";
    /**
     * @ColumnTransformer:用于进行实体属性存入/取出数据库的转换，在作为HQl查询时也会自动转换
     */
    @ColumnTransformer(write = "?::jsonb")
    @Convert(converter = DynamicAttributes.class)
    @Column(name = "attributes", columnDefinition = XConstant.JSONB)
    private DynamicAttributes attributes = new DynamicAttributes();

    public abstract String getNumber();

    public abstract void setNumber(String number);

    public abstract String getName();

    public abstract void setName(String name);

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, XAttribute> getAttributes() {
        return this.attributes.getAttributes();
    }

    public void setAttributes(DynamicAttributes attributes) {
        this.attributes = attributes;
    }

    public Object getAttributeValue(String field) {
        if (field == null || field.trim().isEmpty())
            return null;

        Method method = null;
        try {
            String isName = "is" + field.substring(0, 1).toUpperCase(Locale.ENGLISH) + field.substring(1);
            method = this.getClass().getMethod(isName, new Class[0]);
        } catch (NoSuchMethodException | SecurityException e) {
        }
        if (method == null) {
            try {
                String getName = "get" + field.substring(0, 1).toUpperCase(Locale.ENGLISH) + field.substring(1);
                method = this.getClass().getMethod(getName, new Class[0]);
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }
        if (method == null) {
            return this.attributes.getAttributes().get(field);
        } else {
            try {
                return method.invoke(this, new Object[0]);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new XException(e);
            }
        }
    }

    public String getDisplay() {
        return this.getNumber() + ", " + this.getName();
    }

    public static String getIdentityField() {
        return "number";
    }
}
