package com.flame.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class FlameUtils extends XConstants {
    static final Pattern DIGISET_PATTERN = Pattern.compile("[0-9]*");
    static final Pattern NUMERIC_PATTERN = Pattern.compile("[0-9]+(\\.[0-9]+)?");
    static final Pattern INTEGER_PATTERN = Pattern.compile("[0-9]+(\\.0+)?");
    static final Pattern PATTERN_OID = Pattern.compile("(OR:)?([a-z0-9A-Z]+(\\.))+[a-z0-9A-Z]+(:[0-9]+)");

    public static File mkdirs(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        file.mkdirs();
        return file;
    }

    public static String[] splitOid(String oid) {
        if (isBlank(oid)) {
            return new String[0];
        }

        Matcher matcher = PATTERN_OID.matcher(oid);
        if (!matcher.matches())
            throw new com.flame.util.XException("Oid(" + oid + ") is incorrect Oid format.");

        String[] splits = oid.split(":");
        if (splits.length == 2) {
            return new String[] { "OR", splits[0], splits[1] };
        } else {
            return splits;
        }
    }

    /**
     * FlameUtils.capitalize(null)  = null
     * FlameUtils.capitalize("")    = ""
     * FlameUtils.capitalize("cat") = "Cat"
     * FlameUtils.capitalize("cAt") = "CAt"
     *
     * @param m
     * @return
     */
    public static String capitalize(String m) {
        if (m == null)
            return null;
        if (isBlank(m))
            return m;

        if (m.charAt(0) > 96 && m.charAt(0) < 123) {
            return (char) (m.charAt(0) - 32) + m.substring(1);
        } else {
            return m;
        }
    }

    /**
     * Capitalizes a String changing the first letter to title case as per Character.toTitleCase(char). No other letters are changed.
     *
     * @param m
     * @return
     */
    public static String capitalise(String m) {
        if (m == null)
            return null;
        if (isBlank(m))
            return m;

        if (m.charAt(0) > 64 && m.charAt(0) < 91) {
            return (char) (m.charAt(0) + 32) + m.substring(1);
        } else {
            return m;
        }
    }

    public static String trimPrefix(String string, char c) {
        if (isBlank(string))
            return "";
        if (c == '\0')
            return string;
        char[] array = string.toCharArray();
        int l = array.length;
        for (int i = 0; i < l; i++) {
            if (array[i] != c) {
                return string.substring(i);
            }
        }
        return string;
    }

    public static String trim(String string, char c) {
        if (isBlank(string))
            return "";
        if (c == '\0')
            return string;

        String result = string;
        char[] array = result.toCharArray();
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] != c) {
                result = result.substring(i);
                break;
            }
        }
        array = result.toCharArray();
        len = array.length;
        for (int i = len - 1; i > 0; i--) {
            if (array[i] != c) {
                result = result.substring(0, i + 1);
                break;
            }
        }

        return result;
    }

    public static boolean equals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }

    public static int indexOf(String str, char searchChar) {
        return isEmpty(str) ? -1 : str.indexOf(searchChar);
    }

    public static int indexOf(String str, char searchChar, int startPos) {
        return isEmpty(str) ? -1 : str.indexOf(searchChar, startPos);
    }

    public static int indexOf(String str, String searchStr) {
        return str != null && searchStr != null ? str.indexOf(searchStr) : -1;
    }

    public static boolean isDigist(String str) {
        if (str == null)
            return false;

        Matcher isNum = DIGISET_PATTERN.matcher(str);
        return isNum.matches();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static boolean isEmpty(String value) {
        return (value == null || value.isEmpty());
    }
    
    public static String blankOutput(String value, String defaultValue) {
        if (isBlank(value))
            return defaultValue;
        else 
            return value;
    }

    public static boolean isNumeric(String str) {
        if (str == null)
            return false;

        Matcher isNum = NUMERIC_PATTERN.matcher(str);
        return isNum.matches();
    }

    public static boolean isInteger(String str) {
        if (str == null)
            return false;

        Matcher isNum = INTEGER_PATTERN.matcher(str);
        if (isNum.matches()) {
            return true;
        }
        return false;
    }

    public static Long getRandomConst() {
        return Double.valueOf(Math.random() * 1000000).longValue();
    }

    public static String getBase64Encode(String value) {
        return new String(Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    public static String getBase64Decode(String value) {
        return new String(Base64.getDecoder().decode((String) value), StandardCharsets.UTF_8);
    }

    public static String getLastSplit(String string, String split) {
        if (isBlank(string) || isBlank(split))
            return "";
        if (".".equals(split))
            split = "\\.";
        String[] splits = string.split(split);
        return splits[splits.length - 1];
    }

    public static String getFirstSplit(String string, String split) {
        if (FlameUtils.isBlank(string))
            return "";
        if (".".equals(split))
            split = "\\.";
        String[] splits = string.split(split);
        return splits[0];
    }
}
