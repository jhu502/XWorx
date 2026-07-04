package com.flame.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class PropertyUtil {

    public static Object getProperty(Object object, String property) {
        if (object == null || property == null)
            return null;

        if (object instanceof Map) {
            return ((Map<?, ?>) object).get(property);
        } else {
            String name = "get" + StringUtil.capitalize(property);
            Method method = BeanUtils.findMethod(object.getClass(), name);
            if (method == null)
                return null;

            try {
                return method.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new XException(e);
            }
        }
    }
}
