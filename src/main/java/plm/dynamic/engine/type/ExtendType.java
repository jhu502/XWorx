package plm.dynamic.engine.type;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flame.util.XException;

public class ExtendType implements Comparable<ExtendType>, Serializable {
	private static final long serialVersionUID = 1L;
	private static Pattern Digiset_Pattern = Pattern.compile("[0-9]*");
	private static Pattern Numeric_Pattern = Pattern.compile("[0-9]+(\\.[0-9]+)?");
	private static Pattern Integer_Pattern = Pattern.compile("[0-9]+(\\.0+)?");
	protected static ObjectMapper mapper = new ObjectMapper();

	static {
		//mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static <T extends ExtendType> T valueOf(String jsonStr, Class<T> typeClass) {
		try {
			return mapper.readValue(jsonStr, typeClass);
		} catch (Exception e) {
			throw new XException(e.getMessage(), e);
		}
	}
	
	public static <T extends ExtendType> T newInstance(Class<T> typeClass) {
		try {
			Method getDefaultValue = typeClass.getMethod("getDefaultValue", new Class<?>[0]);
			String defaultValue = (String) getDefaultValue.invoke(null, new Object[0]);
			return mapper.readValue(defaultValue, typeClass);
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	public static String getDefaultValue() {
		return "{}";
	}

	public ExtendType getSettings(Object input) {
		return null;
	}
	
	/**
	 * 复杂参数选配提交值被调用，去检查数据合规性
	 */
	public void validate() {
	}

	@Override
	public int compareTo(ExtendType o) {
		return 1;
	}

	@Override
	public String toString() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return "{}";
	}

	@JsonIgnore
	public ExtendType clone() {
		return this;
	}

	public static boolean isBlank(Object object) {
		if (object == null)
			return true;

		if (object instanceof String) {
			String str = (String) object;
			if (str.isEmpty() || str.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDigist(String str) {
		Matcher isNum = Digiset_Pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		}
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
}
