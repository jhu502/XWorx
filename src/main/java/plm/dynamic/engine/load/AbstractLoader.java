package plm.dynamic.engine.load;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import plm.part.XPart;
import plm.part.XPartUsageLink;

public abstract class AbstractLoader implements Loader {
	//参数(Parameter)在运算引擎内部会被重新命一个别名：'P'+ i，threadVariable就是i的计数器
	private static ThreadLocal<Integer> threadVariable = new ThreadLocal<>();

	public synchronized String genParameterAlias() {
		Integer i = threadVariable.get();
		if (i == null) {
			i = 0;
		}
		threadVariable.set(i + 1);

		return "P" + i;
	}

	public static void clearParameterGenor() {
		threadVariable.remove();
	}

	protected String trimCase(String value) {
		if (value == null)
			return value;
		int length = value.length();
		if (length > 2 && value.startsWith("\"") && value.endsWith("\"")) {
			return value.substring(1, length - 1);
		} else
			return value;
	}

	protected boolean isBlank(Object value) {
		if (value == null)
			return true;
		if (value instanceof String) {
			if (value.toString().isEmpty() || value.toString().trim().isEmpty()) {
				return true;
			}
		}

		return false;
	}

	protected Object dynamicMappingValue(XPart part, XPartUsageLink link, String dyMapping) {
		if (part.getAttributes().containsKey(dyMapping)) {
			return part.getAttributeValue(dyMapping);
		} else {
			Method method = this.getMethod(part, "get" + upper(dyMapping), new Class<?>[0]);
			if (method == null) {
				method = this.getMethod(part, "is" + upper(dyMapping), new Class<?>[0]);
			}
			if (method != null) {
				try {
					Object value = method.invoke(part);
					return value == null ? "-" : value;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				}
			}
			if (method == null) {
				method = this.getMethod(link, "get" + upper(dyMapping), new Class<?>[0]);
			}
			if (method == null) {
				method = this.getMethod(link, "is" + upper(dyMapping), new Class<?>[0]);
			}
			if (method != null) {
				try {
					Object value = method.invoke(link);
					return value == null ? "-" : value;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				}
			}

			return "-";
		}
	}

	protected Method getMethod(Object object, String name, Class<?>[] params) {
		if (object == null)
			return null;

		try {
			return object.getClass().getMethod(name, params);
		} catch (NoSuchMethodException | SecurityException e) {
		}

		return null;
	}

	public String upper(String string) {
		char[] chars = string.toCharArray();
		chars[0] -= 32;
		return String.valueOf(chars);
	}
}
