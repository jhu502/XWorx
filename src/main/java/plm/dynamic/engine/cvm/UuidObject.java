package plm.dynamic.engine.cvm;

import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UuidObject implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+(\\.[0-9]+)?");
	private static final Pattern INTEGER_PATTERN = Pattern.compile("[0-9]+(\\.0+)?");
	private String uuid = UUID.randomUUID().toString();

	public String getUUID() {
		return this.uuid;
	}
	
	protected void setUUID(String uuid) {
		this.uuid = uuid;
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

	public static boolean isNumeric(String str) {
		Matcher isNum = NUMBER_PATTERN.matcher(str);
		if (isNum.matches()) {
			return true;
		}
		return false;
	}

	public static boolean isInteger(String str) {
		Matcher isNum = INTEGER_PATTERN.matcher(str);
		if (isNum.matches()) {
			return true;
		}
		return false;
	}

	public static String S(String format, Object... args) {
		return String.format(format, args);
	}
}
