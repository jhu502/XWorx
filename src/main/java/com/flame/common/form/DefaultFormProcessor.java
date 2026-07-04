package com.flame.common.form;

import com.flame.xui.XCommandBean;
import com.flame.config.FlameConfiguration;
import com.flame.localize.LocalizationHelper;
import com.flame.util.FlameUtils;
import com.flame.orm.XObject;
import com.flame.util.XException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

public class DefaultFormProcessor implements ObjectFormProcessor {
	protected static final Logger logger = LoggerFactory.getLogger(DefaultFormProcessor.class);

	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = new FormResult();

		formResult.setStatus(FormStatus.SUCCESS);
		formResult.setMessage("Successfully");
		formResult.setData(Collections.EMPTY_MAP);

		return formResult;
	}

	protected void checkRequiredField(Object value, String name) throws XException {
		if (FlameUtils.isBlank(value)) {
			throw new XException(LocalizationHelper.get("promptRequiredField", LocalizationHelper.get(name)));
		}
	}

	public <T> T convertUnsafe(Map<String, Object> map, Class<T> clazz) {
		if (map == null)
			return null;

		ConversionService service = FlameConfiguration.getBean(ConversionService.class);
		try {
			Constructor<T> c = clazz.getConstructor();
			T t = c.newInstance();

			Field[] fields = this.getDeclaredFields(t.getClass());
			for (Field field : fields) {
				int mod = field.getModifiers();
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue;
				}

				field.setAccessible(true);
				Object v = map.get(field.getName());
				if (v == null) {
					continue;
				}
				if (field.getType().equals(v.getClass()) || field.getType().isAssignableFrom(v.getClass())) {
					field.set(t, v);
				} else {
					Object _v = service.convert(v, field.getType());
					field.set(t, _v);
				}
			}
			return t;
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	public <T> T convertUnsafe(Map<String, Object> map, T t) {
		if (map == null || t == null)
			return null;

		ConversionService service = FlameConfiguration.getBean(ConversionService.class);
		try {
			Field[] fields = this.getDeclaredFields(t.getClass());
			for (Field field : fields) {
				int mod = field.getModifiers();
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue;
				}

				field.setAccessible(true);
				Object v = map.get(field.getName());
				if (v == null) {
					continue;
				}
				if (field.getType().equals(v.getClass()) || field.getType().isAssignableFrom(v.getClass())) {
					field.set(t, v);
				} else {
					Object _v = service.convert(v, field.getType());
					field.set(t, _v);
				}
			}
			return t;
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	public <T> T convertSafe(Map<String, Object> map, Class<T> clazz) {
		try {
			Constructor<T> c = clazz.getConstructor();
			return convertSafe(map, c.newInstance());
		} catch (XException e) {
			throw e;
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	public <T> T convertSafe(Map<String, Object> map, T t) {
		if (map == null || t == null)
			return t;

		ConversionService service = FlameConfiguration.getBean(ConversionService.class);
		Map<String, Method> methodMap = getDeclaredMethods(t.getClass());
		for (Entry<String, Object> entry : map.entrySet()) {
			Method method = methodMap.get("set" + FlameUtils.capitalize(entry.getKey()));
			method = method == null ? methodMap.get("set" + FlameUtils.capitalise(entry.getKey())) : method;
			if (method != null) {
				Parameter[] params = method.getParameters();
				Object value = entry.getValue();
				try {
					if (params[0].getType().equals(value.getClass()) || params[0].getType().isAssignableFrom(value.getClass())) {
						method.invoke(t, value);
					} else {
						if (service.canConvert(value.getClass(), params[0].getType())) {
							method.invoke(t, service.convert(value, params[0].getType()));
						}
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new XException(e);
				}
			}
		}

		return t;
	}

	private Field[] getDeclaredFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		while (clazz != null) {
			fields.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
			clazz = clazz.getSuperclass();
			if (XObject.class.equals(clazz))
				clazz = null;
		}
		return fields.toArray(new Field[0]);
	}

	private Map<String, Method> getDeclaredMethods(Class<?> clazz) {
		Map<String, Method> result = new HashMap<>();
		while (clazz != null) {
			for (Method method : clazz.getDeclaredMethods()) {
				int mod = method.getModifiers();
				if (!Modifier.isStatic(mod) && Modifier.isPublic(mod)) {
					if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
						result.put(method.getName(), method);
					}
				}
			}
			clazz = clazz.getSuperclass();
			if (XObject.class.equals(clazz)) {
				clazz = null;
			}
		}
		return result;
	}

	public static String S(String format, Object... args) {
		return String.format(format, args);
	}
}
