package plm.part.util;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilityTool {
	static Pattern Digiset_Pattern = Pattern.compile("[0-9]*");
	static Pattern Numeric_Pattern = Pattern.compile("[0-9]+(\\.[0-9]+)?");
	static Pattern Integer_Pattern = Pattern.compile("[0-9]+(\\.0+)?");

	public static TreeSet<Integer> getAllowValues(String valueRange) {
		TreeSet<Integer> tList = new TreeSet<Integer>();
		if (!isBlank(valueRange)) {
			if (valueRange.contains(",")) {
				for (String aValue : valueRange.split(",")) {
					if (isBlank(aValue)) {
						continue;
					}
					tList.addAll(getAllowValues(aValue));
				}
			} else if (valueRange.contains("-")) {
				String[] valueArray = valueRange.split("-");
				if (valueArray != null && valueArray.length == 2) {
					int start = Integer.parseInt(valueArray[0]);
					int end = Integer.parseInt(valueArray[1]);
					for (int i = start; i <= end; i++) {
						tList.add(i);
					}
				}
			} else {
				tList.add(Integer.parseInt(valueRange));
			}

		}
		return tList;
	}

	public static String clearMinusSign(String strs) {
		return strs.replace("-", "");
	}

	public static String formatString(String inStr, String format, boolean bool) {
		int i = format.length();
		if ((inStr == null) || (inStr == ""))
			return format;
		if (i <= 0)
			return inStr;
		String temp = inStr;
		int j = temp.length();
		if (i > j) {
			String t1 = format.substring(0, i - j);
			if (bool)
				return temp + t1;
			else
				return t1 + temp;
		} else
			return temp;
	}

	public static String formatString(String inStr, int length, boolean bool) {
		String format = "                                                                     ";
		if ((inStr == null) || (inStr == ""))
			return format.substring(0, length);
		if (length <= 0)
			return inStr;
		String temp = inStr;
		int j = temp.length();
		if (length > j) {
			String t1 = format.substring(0, length - j);
			if (bool)
				return temp + t1;
			else
				return t1 + temp;
		} else
			return temp;
	}

	public static String trimString(String inStr, int length, boolean bool) {
		if ((inStr == null) || (inStr == ""))
			return "";
		int l0 = inStr.length();
		if (length <= 0 || length >= l0)
			return inStr;
		String temp = inStr;
		if (bool) {
			return temp.substring(0, length);
		} else {
			return temp.substring(l0 - length);
		}
	}

	public static boolean isDigist(String str) {
		Matcher isNum = Digiset_Pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		}
		return false;
	}

	public static boolean isBlank(String value) {
		if (value == null)
			return true;

		if (value.isEmpty() || value.trim().isEmpty())
			return true;

		return false;
	}

	public static boolean isNumeric(String str) {
		Matcher isNum = Numeric_Pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		}
		return false;
	}

	public static boolean isInteger(String str) {
		Matcher isNum = Integer_Pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		}
		return false;
	}

	public static Object toObject(String input) {
		if (Numeric_Pattern.matcher(input).matches()) {
			if (Integer_Pattern.matcher(input).matches()) {
				return Integer.valueOf(input);
			} else if (Integer_Pattern.matcher(input).matches()) {
				return Double.valueOf(input).longValue();
			} else {
				return Double.valueOf(input);
			}
		} else {
			return input;
		}
	}

	public static String trimPrefix(String str, char c) {
		if (str == null)
			return "";
		if (c == '\0')
			return str;
		char[] cs = str.toCharArray();
		int l = cs.length;
		for (int i = 0; i < l; i++) {
			if (cs[i] != c) {
				return str.substring(i);
			}
		}
		return str;
	}

	public static void main(String[] args) {
		String hujin = "www.sina.com.cn";
		System.out.println(trimString(hujin, 6, false));
	}
}
