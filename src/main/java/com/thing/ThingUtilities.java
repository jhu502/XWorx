package com.thing;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flame.annotations.XConfig;
import com.flame.annotations.XParam;
import com.flame.annotations.XService;
import com.flame.orm.AbstractEntity;
import com.flame.orm.XObject;
import com.flame.orm.XPersistable;
import com.flame.thing.Argument;
import com.flame.thing.IModelManaged;
import com.flame.thing.IServiceDefinition;
import com.flame.thing.IThingModel;
import com.flame.type.XBaseType;
import com.flame.util.XException;
import com.google.common.collect.Sets;

import jakarta.persistence.Column;

public class ThingUtilities {
	private static final Pattern PATTERN_OID = Pattern.compile("(OR:)?([a-z0-9A-Z]+(\\.))+[a-z0-9A-Z]+(:[0-9]+)");
	private static final Pattern PATTERN_VAR = Pattern.compile("[a-zA-Z]([a-z0-9A-Z_])*");
	private static final Pattern PATTERN_THG = Pattern.compile("([A-Za-z_])([A-Za-z0-9_]*)(:)([^\\s\\n]*)"); //([A-Za-z_])([A-Za-z0-9_]*)(:)([A-Za-z_])([A-Za-z0-9_]*)

	public static String[] isOid(String oid) {
		if (oid == null)
			return new String[0];

		Matcher matcher = PATTERN_OID.matcher(oid);
		if (!matcher.matches())
			throw new com.flame.util.XException("Oid(" + oid + ") is incorrect Oid format.");

		String[] splits = oid.split(":");
		if (splits.length == 2) {
			return new String[]{"OR", splits[0], splits[1]};
		} else {
			return splits;
		}
	}

	public static boolean checkVarFormat(String var) {
		Matcher matcher = PATTERN_VAR.matcher(var);
		return matcher.matches();
	}

	/**
	 * Thing Identity格式为：
	 * 		XUser:Administrator
	 * 
	 * @param ident
	 * @return
	 */
	public static boolean checkThingIdentity(String ident) {
		Matcher matcher = PATTERN_THG.matcher(ident);
		return matcher.matches();
	}

	/**
	 * Entity Identity格式为：
	 * 		OR:xw.auths.XUser:1221
	 * 
	 * @param ident
	 * @return
	 */
	public static boolean checkEntityIdentity(String ident) {
		if (ident == null) 
			return false;
		
		Matcher matcher = PATTERN_OID.matcher(ident);
		return matcher.matches();
	}

	public static List<Field> getDeclaredFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		while (clazz != null) {
			fields.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	public static List<Field> getDeclaredFields(Class<?> clazz, Set<Class<?>> excludes) {
		List<Field> fields = new ArrayList<>();
		while (clazz != null) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			Class<?> superCls = clazz.getSuperclass();
			if (excludes.contains(superCls)) {
				clazz = null;
			} else {
				clazz = clazz.getSuperclass();
			}
		}
		return fields;
	}

	public static List<String> getColumnAttributes(IThingModel model) {
		List<String> result = new ArrayList<String>();
		if (model == null)
			return result;

		Class<?> modelCls = model.getEntityClass();
		for (Field field : getDeclaredFields(modelCls)) {
			if (String.class.equals(field.getType())) {

				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					result.add(field.getName());
				}
			}
		}

		return result;
	}

	public static List<Map<String, Object>> getThingConfiguration(Class<?> configClass) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		List<Field> list = getDeclaredFields(configClass, Sets.newHashSet(new Class<?>[] { XObject.class, AbstractEntity.class }));
		for (Field field : list) {
			XConfig config = field.getAnnotation(XConfig.class);
			if (config != null) {
				Map<String, Object> row = new HashMap<String, Object>();
				row.put("name", config.name());
				row.put("display", config.friendlyName());
				row.put("type", config.baseType().getDisplay());
				row.put("description", config.description());
				row.put("required", config.required());
				row.put("created", config.created());
				row.put("modified", config.modified());
				row.put("defaultValue", config.defaultValue());

				result.add(row);
			}
		}

		return result;
	}

	public static List<Map<String, Object>> getThingConfiguration(XPersistable persist) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		List<Field> list = getDeclaredFields(persist.getClass(), Sets.newHashSet(new Class<?>[] { XObject.class, AbstractEntity.class }));
		for (Field field : list) {

			XConfig config = field.getAnnotation(XConfig.class);
			if (config != null) {
				try {

					Map<String, Object> row = new HashMap<String, Object>();
					row.put("name", config.name());
					row.put("display", config.friendlyName());
					row.put("type", config.baseType().getDisplay());
					row.put("description", config.description());
					row.put("required", config.required());
					row.put("created", config.created());
					row.put("modified", config.modified());
					row.put("defaultValue", config.defaultValue());

					Object value = "";
					try {
						if (XBaseType.BOOLEAN.equals(config.baseType())) {
							String mname = "is" + config.name().substring(0, 1).toUpperCase() + config.name().substring(1);
							Method method = persist.getClass().getMethod(mname);
							value = method.invoke(persist);
						} else {
							String mname = "get" + config.name().substring(0, 1).toUpperCase() + config.name().substring(1);
							Method method = persist.getClass().getMethod(mname);
							value = method.invoke(persist);
						}

					} catch (NoSuchMethodException e) {
						field.setAccessible(true);
						value = field.get(persist);
					}
					row.put("value", value);

					result.add(row);
				} catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException e) {
					throw new XException(e);
				}
			}
		}

		return result;
	}

	public static List<Map<String, Object>> getThingServices(Class<?> thingClass) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		Method[] methods = thingClass.getDeclaredMethods();
		for (Method method : methods) {
			XService ftservice = method.getAnnotation(XService.class);
			if (ftservice != null) {
				Map<String, Object> row = new HashMap<String, Object>();
				row.put("icon", ftservice.serviceType().getIcon());
				row.put("type", ftservice.serviceType().name());
				row.put("result", ftservice.resultType().getDisplay());
				row.put("name", ftservice.name());
				StringBuffer input = new StringBuffer();
				for (XParam param : ftservice.params()) {
					if (input.length() == 0) {
						input.append(param.type().getDisplay()).append(" ").append(param.name());
					} else {
						input.append(", ").append(param.type().getDisplay()).append(" ").append(param.name());
					}
				}
				row.put("input", input.toString());

				result.add(row);
			}
		}

		return result;
	}

	public static Map<String, Object> getThingServiceInfo(IModelManaged thingEntity, String serviceName) {
		Map<String, Object> result = new HashMap<String, Object>();
		IThingModel thingModel = (IThingModel) thingEntity.getThingModel();
		
		Method method = null;
		Method[] methods = thingModel.getModelClass().getMethods();
		for (Method _method : methods) {
			if (_method.getName().equals(serviceName)) {
				method = _method;
			}
		}
		
		if (method != null) {
			XService ftservice = method.getAnnotation(XService.class);
			if (ftservice != null) {
				result.put("resultType", ftservice.resultType());
				result.put("name", ftservice.name());
				List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
				for (XParam param : ftservice.params()) {
					Map<String, Object> row = new LinkedHashMap<String, Object>();
					row.put("name", param.name());
					row.put("type", param.type());
					params.add(row);
				}
				result.put("params", params);
				
				return result;
			}
		}
		
		IServiceDefinition serviceDef = thingModel.getServiceDefinition(serviceName);
		if (serviceDef != null) {
			result.put("resultType", serviceDef.getResultType());
			result.put("name", serviceDef.getName());
			List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
			for (Argument param : serviceDef.getArguments()) {
				Map<String, Object> row = new HashMap<String, Object>();
				row.put("name", param.getName());
				row.put("type", param.getBaseType());
				params.add(row);
			}
			result.put("params", params);
			
			return result;
		}
		
		return result;
	}

	public static void main(String[] args) {
		System.out.println(isOid("OR:com.thing.entity.XThingModel:36"));
	}
}
