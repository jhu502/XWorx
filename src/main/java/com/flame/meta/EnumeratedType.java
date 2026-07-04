package com.flame.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EnumeratedType<T extends EnumeratedType<T>> {
	protected static final Logger logger = LoggerFactory.getLogger(EnumeratedType.class);
	protected final static Map<String, Map<String, ? extends EnumeratedType<?>>> valueMap = new ConcurrentHashMap<>();
	protected String name;
	protected String display;

	public EnumeratedType(String name, String display) {
		this.name = name;
		this.display = display;
	}

	public String getName() {
		return this.name;
	}

	public String getDisplay() {
		return this.display;
	}

	public String getDisplay(Locale locale) {
		return this.display;
	}

	private static void init(Class<?> clazz) {
		try {
			Method method = clazz.getMethod("init");
			method.invoke(null);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			logger.debug("Class:" + clazz.getName() + " failed to invoke init() method.");
		}
	}

	public static <T extends EnumeratedType<T>> T toEnumeratedType(String name, Class<T> clazz) {
		if (name == null || clazz == null)
			return null;

		Map<String, ? extends EnumeratedType<T>> enumMap = (Map<String, ? extends EnumeratedType<T>>) valueMap.get(clazz.getName());
		if (enumMap == null) {
			init(clazz);
		}
		enumMap = (Map<String, ? extends EnumeratedType<T>>) valueMap.get(clazz.getName());
		if (enumMap == null) {
			enumMap = new LinkedHashMap<>();
		}
		return (T) enumMap.get(name);
	}

	public static <T extends EnumeratedType<T>> void addEnumeratedType(T enumType) {
		Map<String, T> enumMap = (Map<String, T>) valueMap.get(enumType.getClass().getName());
		if (enumMap == null) {
			enumMap = new LinkedHashMap<>();
			valueMap.put(enumType.getClass().getName(), enumMap);
		}
		enumMap.put(enumType.getName(), enumType);
	}

	public static <T extends EnumeratedType<T>> Collection<T> values(Class<T> clazz) {
		Map<String, ? extends EnumeratedType<T>> enumMap = (Map<String, ? extends EnumeratedType<T>>) valueMap.get(clazz.getName());
		return (Collection<T>) enumMap.values();
	}
}
