package com.flame.type;

import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.Date;

import com.flame.localize.LocalizationHelper;
import com.flame.type.primitive.*;
import com.flame.util.XException;

public enum XBaseType implements IBaseType {
	OBJECT((byte) -200, XObjectPrimitive.class), //
	NOTHING((byte) -1, XNothingPrimitive.class), //
	STRING((byte) 0, XStringPrimitive.class), //
	NUMBER((byte) 1, XNumberPrimitive.class), //
	BOOLEAN((byte) 2, XBooleanPrimitive.class), //
	DATETIME((byte) 3, XDatetimePrimitive.class), //
	TIMESPAN((byte) 4, XTimespanPrimitive.class), //
	INFOTABLE((byte) 5, XInfoTablePrimitive.class), //
	LOCATION((byte) 6, XLocationPrimitive.class), //
	XML((byte) 7, XXMLPrimitive.class), //
	JSON((byte) 8, XJSONPrimitive.class), //
	QUERY((byte) 9, XObjectPrimitive.class), //
	IMAGE((byte) 10, XImagePrimitive.class), //
	HYPERLINK((byte) 11, XStringPrimitive.class), //
	IMAGELINK((byte) 12, XStringPrimitive.class), //
	PASSWORD((byte) 13, XPasswordPrimitive.class), //
	HTML((byte) 14, XHtmlPrimitive.class), //
	TEXT((byte) 15, XTextPrimitive.class), //
	TAGS((byte) 16, XTagsPrimitive.class), //
	SCHEDULE((byte) 17, XObjectPrimitive.class), //
	VARIANT((byte) 18, XVariantPrimitive.class), //
	GUID((byte) 20, XGuidPrimitive.class), //
	BLOB((byte) 21, XBlobPrimitive.class), //
	INTEGER((byte) 22, XIntegerPrimitive.class), //
	LONG((byte) 23, XLongPrimitive.class); //

	private byte _code;
	private transient Class<? extends IPrimitiveType<?>> _primitive;

	XBaseType(byte code, Class<? extends IPrimitiveType<?>> _primitive) {
		this._code = code;
		this._primitive = _primitive;
	}

	public byte getCode() {
		return this._code;
	}

	public String getName() {
		return this.name();
	}

	public String getConverterName() {
		return "com.thingworx.flame.converter." + this._primitive.getSimpleName() + "Converter";
	}

	public Class<? extends IPrimitiveType<?>> getPrimitive() {
		return this._primitive;
	}

	public IPrimitiveType<?> getPrimitive(Object data) {
		try {
			Constructor<?> constructor = this.getPrimitive().getConstructor();
			IPrimitiveType<?> primitiveType = (IPrimitiveType<?>) constructor.newInstance();
			if (primitiveType instanceof AbstractPrimitive) {
				@SuppressWarnings("unchecked")
				AbstractPrimitive<Object> xPrimitive = (AbstractPrimitive<Object>) primitiveType;
				xPrimitive.setValue(data);
			}
			return primitiveType;
		} catch (XException e) {
			throw e;
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	public String toString() {
		return this.name();
	}

	public Class<?> getPrototype() {
		Class<?> result = Object.class;
		switch (this) {
		case STRING:
			result = String.class;
			break;
		case BOOLEAN:
			result = Boolean.class;
			break;
		case LONG:
			result = Long.class;
			break;
		case NUMBER:
			result = Double.class;
			break;
		case INTEGER:
			result = Integer.class;
			break;
		case INFOTABLE:
			result = XInfoTable.class;
			break;
		case DATETIME:
			result = Date.class;
			break;
		default:
			result = String.class;
		}

		return result;
	}

	public static XBaseType toBaseType(Class<?> clazz) {
		if (clazz == null) {
			return XBaseType.NOTHING;
		} else if (String.class.equals(clazz)) {
			return XBaseType.STRING;
		} else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
			return XBaseType.BOOLEAN;
		} else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
			return XBaseType.LONG;
		} else if (Number.class.equals(clazz)) {
			return XBaseType.NUMBER;
		} else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
			return XBaseType.INTEGER;
		} else if (Timestamp.class.equals(clazz)) {
			return XBaseType.DATETIME;
		} else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
			return XBaseType.NUMBER;
		} else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
			return XBaseType.NUMBER;
		}

		return XBaseType.OBJECT;
	}

	/**
	 * 用来对FlameMessage的参数、FlameResult的返回结果进行解码，格式如下：<BaseType>:<Data>
	 * 	  STRING:SGVsbG86OQ==
	 * 	  LONG:SDSDSEE==
	 * 	  INTEGER:DDSFerDTT4==
	 * 	说明：“:”前面是BaseType类型，“:”后面是数据，根据BaseType然后转换成IPrimitive
	 *
	 * @param data
	 * @return
	 */
	public static IPrimitiveType<?> decodeToPrimitive(String data) {
		if (data == null) {
			return new XNothingPrimitive();
		}

		if ("".equals(data.trim())) {
			return new XStringPrimitive("");
		}

		try {
			int i = data.indexOf(":");
			if (i > -1) {
				String type = data.substring(0, i);
				String value = data.substring(i + 1);
				try {
					XBaseType baseType = XBaseType.valueOf(type);
					Constructor<?> constructor = baseType.getPrimitive().getConstructor(new Class<?>[]{String.class, boolean.class});
					return (IPrimitiveType<?>) constructor.newInstance(value, true);
				}  catch (IllegalArgumentException e) {
					return new XStringPrimitive(data);
				}
			} else {
				return new XStringPrimitive(data);
			}
		} catch (XException e) {
			throw e;
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	public static XBaseType[] characterTypes() {
		return new XBaseType[] { XBaseType.STRING, XBaseType.LONG, XBaseType.NUMBER, XBaseType.BOOLEAN, XBaseType.JSON, XBaseType.INFOTABLE, XBaseType.VARIANT };
	}

	public static XBaseType[] propertyTypes() {
		return new XBaseType[] { XBaseType.STRING, XBaseType.LONG, XBaseType.NUMBER, XBaseType.BOOLEAN, XBaseType.JSON, XBaseType.INFOTABLE, XBaseType.VARIANT };
	}

	public static XBaseType[] parameterTypes() {
		return new XBaseType[] { XBaseType.STRING, XBaseType.LONG, XBaseType.NUMBER, XBaseType.INTEGER, XBaseType.BOOLEAN, XBaseType.JSON };
	}

	public static Class<?>[] toPrototypes(XBaseType[] types) {
		if (types == null)
			return new Class<?>[0];

		Class<?>[] classes = new Class<?>[types.length];
		for (int i = 0; i < types.length; i++) {
			classes[i] = types[i].getPrototype();
		}

		return classes;
	}

	public String getDisplay() {
		String display = LocalizationHelper.get(this.name());
		if (isBlank(display))
			return this.name();
		else
			return display;
	}

	public boolean isBlank(String value) {
		int length = 0;
		if (value == null || (length = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < length; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		XBaseType baseType = XBaseType.valueOf("STRING0");
		System.out.println(baseType);
	}
}
