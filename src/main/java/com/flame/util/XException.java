package com.flame.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author hujin
 * @category util
 * @date 2009.9.15
 * 
 */
public class XException extends RuntimeException {
    private static final long serialVersionUID = 7960570627997714421L;
    private Type type = Type.ERROR;

    public static enum Type {
        INFO, DEBUG, CHECK, ERROR
    }

    public XException(String s) {
        super(s);
    }

    public XException(Type type, String s) {
        super(s);

        if (type != null) {
            this.setType(type);
        }
    }

    public XException(Throwable throwable) {
        super(throwable);
    }

    public XException(String msgId, String msg) {
        super(msgId + "~" + msg);
    }

    public XException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public static XException wrap(Throwable throwable, String message, Object... params) {
        return new XException(String.format(message, params), throwable);
    }

    public static XException wrap(String msg, Object... params) {
        return new XException(String.format(msg, params));
    }

    public static XException packXException(Throwable throwable) {
        if (throwable instanceof UndeclaredThrowableException) {
            UndeclaredThrowableException e = (UndeclaredThrowableException) throwable;
            throwable = e.getCause();
        }
        if (throwable instanceof InvocationTargetException) {
            InvocationTargetException e = (InvocationTargetException) throwable;
            throwable = e.getCause();
        }
        if (throwable instanceof XException)
            return (XException) throwable;
        else
            return new XException(throwable);
    }

    public static void throwException(String s) {
        throw new XException(s);
    }

    public static void throwException(Throwable throwable) {
        if (throwable instanceof UndeclaredThrowableException) {
            UndeclaredThrowableException e = (UndeclaredThrowableException) throwable;
            throwable = e.getCause();
        }
        if (throwable instanceof InvocationTargetException) {
            InvocationTargetException e = (InvocationTargetException) throwable;
            throwable = e.getCause();
        }
        if (throwable instanceof XException)
            throw (XException) throwable;
        else
            throw new XException(throwable);
    }
}
