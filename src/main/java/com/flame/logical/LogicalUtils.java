package com.flame.logical;

import java.util.ArrayList;
import java.util.List;

import com.flame.orm.PersistenceHelper;
import com.flame.util.FlameUtils;
import com.flame.util.XException;

/**
 * @    用来分隔Type与Instance;
 * |    对协议的内部进行分隔;
 * ~    分隔上下文;
 * ^    跳转/类型转换;
 */
public class LogicalUtils {
    public static final String OID = "OID"; // OID|OR:plm.part.XPart:112022
    public static final String DATA = "DATA";   // DATA|java.lang.String|字符串数据12323
    public static final String XTYPE = "XTYPE"; // XTYPE|plm.part.XPart|com.flame.ManufacturePart~XCI|122112
    public static final String XCI = "XCI"; // XCI|112222|1|112221
    public static final String MBA = "MBA"; // MBA|name             MBA|name|Golf_Car
    public static final String IBA = "IBA"; // IBA|projectName      IBA|projectName|M20Project
    private static final String[] array = {"~XTYPE|", "~XCI|", "~MBA|", "~IBA|", "^XTYPE|", "^XCI|", "^MBA|", "^IBA|"};

    public static class ProtocolExecutor {
    }

    public static boolean isXTYPE(String sentence) {
        if (FlameUtils.isBlank(sentence))
            return false;

        String xtype = getXTYPEContent(sentence);
        if (FlameUtils.isNotBlank(xtype)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getXTYPEContent(String sentence) {
        if (FlameUtils.isBlank(sentence))
            return null;

        if (sentence.startsWith("XTYPE|")) {
            return sentence.substring(6);
        } else if (sentence.startsWith("@XTYPE|")) {
            return sentence.substring(7);
        } else if (sentence.startsWith("~XTYPE|")) {
            return sentence.substring(8);
        } else if (sentence.startsWith("^XTYPE|")) {
            return sentence.substring(8);
        } else {
            return null;
        }
    }

    public static boolean isMBA(String sentence) {
        if (FlameUtils.isBlank(sentence))
            return false;

        String mba = getMBAContent(sentence);
        if (FlameUtils.isNotBlank(mba)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getMBAContent(String sentence) {
        if (FlameUtils.isBlank(sentence))
            return null;

        if (sentence.startsWith("MBA|")) {
            return sentence.substring(4);
        } else if (sentence.startsWith("@MBA|")) {
            return sentence.substring(5);
        } else if (sentence.startsWith("~MBA|")) {
            return sentence.substring(5);
        } else if (sentence.startsWith("^MBA|")) {
            return sentence.substring(5);
        } else {
            return null;
        }
    }

    public static Object[] toMBAArray(String sentence) {
        String mba = getMBAContent(sentence);
        if (mba == null)
            return null;

        int index = mba.indexOf("|");
        if (index > 0) {
            return new Object[]{mba.substring(0, index), mba.substring(index)};
        } else {
            return new Object[]{mba};
        }
    }

    public static void parseGrammar(String sentence, List<String> protocols) {
        if (FlameUtils.isBlank(sentence))
            return;

        int index = -2;
        int length = 0;
        for (String protocol : array) {
            int i = sentence.indexOf(protocol);
            if (i != -1 && i > index) {
                index = i;
                length = protocol.length();
            }
        }

        if (index > 0) {
            protocols.add(sentence.substring(0, index));
            parseGrammar(sentence.substring(index), protocols);
        } else {
            protocols.add(sentence);
        }
    }

    public static Object getObject(String logical) {
        if (FlameUtils.isBlank(logical))
            throw new XException("Input parameter is null.");

        List<String> protocols = new ArrayList<>();
        parseGrammar(logical, protocols);
        if (protocols.isEmpty())
            throw new XException("Logical string isn't match regulation");

        String first = protocols.get(0);
        if (!first.startsWith("XTYPE|"))
            throw new XException("First string must be XTYPE.");

        try {
            Class<?> xtypeCls = Class.forName(getXTYPEContent(first));
            if (protocols.size() > 1) {
                String second = protocols.get(1);
                if (!second.startsWith("@MBA|"))
                    throw new XException("Second logical must be MBA.");

                return PersistenceHelper.service().query(xtypeCls, new Object[][]{LogicalUtils.toMBAArray(second)});
            } else {
                return PersistenceHelper.service().query(xtypeCls, new Object[0][0]);
            }
        } catch (ClassNotFoundException e) {
            throw new XException(e);
        }
    }

    public static void main(String[] args) {
        String contextRef = "XTYPE|xw.context.entity.XOrganization~MBA|number|FLAME";
        List<String> protocols = new ArrayList<>();
        parseGrammar(contextRef, protocols);
        System.out.println(protocols);
    }
}
