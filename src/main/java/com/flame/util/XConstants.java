package com.flame.util;

import java.io.File;

public class XConstants {
    public static final String XWORX_STORAGE = "storage";
    public static final String XWORX_TEMP = "storage" + File.separator + "temp";
    public static final String XWORX_TOMCAT = "storage" + File.separator + "tomcat";
    public static final String XWORX_HOME = "xworx.home";
    public static final String XWORX_JVM_ID = "xworx.jvm.id";

    public static boolean isBlank(Object object) {
        if (object == null)
            return true;

        if (!(object instanceof String))
            return false;
        else
            return isBlank((String) object);
    }

    public static boolean isBlank(String value) {
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
