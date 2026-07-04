package plm.dynamic.engine.exprs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

public class LookUpProperties<S, T> extends Properties {
	private static final long serialVersionUID = 1L;
	private static LookUpProperties<String, String> properties;

	public static LookUpProperties<String, String> getLookUpProperties() {
		if (properties == null) {
			properties = new LookUpProperties<>();
			ResourceBundle bundle = ResourceBundle.getBundle("plm.dynamic.engine.LookUpService");
			Enumeration<String> enums = bundle.getKeys();
			while (enums.hasMoreElements()) {
				String key = enums.nextElement();
				if (key.startsWith("exparser.import.")) {
					String value = bundle.getString(key);
					if (value != null && !"".equals(value.trim())) {
						String name = value.substring(value.lastIndexOf(".") + 1);
						properties.put(name, value);
						properties.put(value, value);
					}
				} else if (key.startsWith("exparser.static.import.")) {
					String value = bundle.getString(key);
					if (value != null && !"".equals(value.trim())) {
						try {
							Class<?> clazz = Class.forName(value);
							for (Entry<String, String> entry : getDeclaredMethods(clazz).entrySet()) {
								properties.put(entry.getKey() + "()", entry.getValue());
							}
							properties.put(value, value);
						} catch (ClassNotFoundException e) {
							System.out.println(e.getMessage());
						}

					}
				}
			}
		}
		return properties;
	}

	private static Map<String, String> getDeclaredMethods(Class<?> clazz) {
		Map<String, String> result = new HashMap<>();
		String classname = clazz == null ? "" : clazz.getName();
		while (clazz != null) {
			for (Method method : clazz.getDeclaredMethods()) {
				int modifiers = method.getModifiers();
				if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
					result.put(method.getName(), classname + "." + method.getName());
				}
			}
			clazz = clazz.getSuperclass();
		}
		return result;
	}

	public static void main(String[] args) {
		Enumeration<?> enums = LookUpProperties.getLookUpProperties().keys();
		while (enums.hasMoreElements()) {
			String key = (String) enums.nextElement();
			System.out.println(key + "    " + LookUpProperties.getLookUpProperties().getProperty(key));
		}
	}
}
