package com.thing.builder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.Argument;
import com.flame.thing.IServiceDefinition;
import com.flame.util.XException;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.thing.entity.XThingModel;

import javassist.Modifier;

@UITreeGrid(idField = "oid", treeField = "name", contextMenu = "ServiceDefinition:serviceDef_menu", rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "oid", hidden = true), //
				@UIColumn(field = "type", hidden = true), //
				@UIColumn(field = "name", width = "300") //
		} //
)
public class ServiceHierarchyBuilder extends AbstractTreeComponentBuilder {
	@Override
	public List<?> getRootNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		//父子XThingModel在展开的过程中会继承相同的ConfigEntity、ThingEntity，因为oid重复会到Tree界面出现错乱
		XObject xobject = commandBean.getPrimaryObj();

		/**
		 * 显示当前XThingModel中动态的定义的ServiceDefinition服务
		 */
		if (xobject instanceof XThingModel) {
			XThingModel thingModel = (XThingModel) xobject;
			if (thingModel != null) {
				Map<String, Object> rootNode = new HashMap<>();
				rootNode.put("oid", thingModel.getOid());
				rootNode.put("name", "<img src='" + thingModel.getIcon() + "'/>" + thingModel.getName());
				rootNode.put("type", "M");
				rootNode.put("state", "open");
				result.add(rootNode);

				List<Map<String, Object>> children = new ArrayList<>(); //getChildHierarchy(thingModel);

				List<IServiceDefinition> serviceList = thingModel.getServiceDefinitions();// tmodelService.listServiceDefinition(thingModel);
				for (IServiceDefinition serviceDef : serviceList) {
					Map<String, Object> serviceNode = new HashMap<>();
					serviceNode.put("oid", thingModel.getOid() + "~" + serviceDef.getOid());
					serviceNode.put("name", "<img src='" + serviceDef.getServiceType().getIcon() + "'/> " + getServiceDisplay(serviceDef));
					serviceNode.put("type", "S");
					serviceNode.put("state", "open");
					children.add(serviceNode);
				}

				XThingModel baseModel = (XThingModel) thingModel.getThingModel();
				if (baseModel != null) {
					Map<String, Object> baseNode = new HashMap<>();
					baseNode.put("oid", baseModel.getOid());
					baseNode.put("name", "<img src='" + baseModel.getIcon() + "'/>" + baseModel.getName());
					baseNode.put("type", "M");
					baseNode.put("state", "closed");
					children.add(baseNode);
				}
				rootNode.put("children", children);
			}
		}
		return result;
	}

	@Override
	public List<?> getNode(XCommandBean commandBean) {
		XUIRowId uiRowId = commandBean.getRowId();
		String oid = uiRowId.getObjectId();
		/**
		 * M: ThingModel
		 * C: ConfigEntity
		 * T: ThingEntity
		 */

		/**
		 * 显示当前ThingModel对应的ThingEntity的方法
		 */
		if (oid.startsWith("M-")) {
			String pid = oid.replace("M-", "");
			try {
				XThingModel thingModel = PersistenceHelper.service().refresh(new ObjectReference<XThingModel>(pid));
				return listServiceDefinition(thingModel);
			} catch (Exception e) {
				throw new XException(e);
			}
		}

		/**
		 * 显示当前ThingModel对应的ConfigEntity的方法
		 */
		if (oid.startsWith("C-")) {
			String configClass = oid.substring(2);
			try {
				Class<?> _class = Class.forName(configClass);
				return listServiceDefinition(uiRowId.getValue(), "C", _class);
			} catch (Exception e) {
				throw new XException(e);
			}
		}

		/**
		 * 显示当前ThingModel对应的ThingEntity的方法
		 */
		if (oid.startsWith("T-")) {
			String thingClass = oid.replace("T-", "");
			try {
				Class<?> _class = Class.forName(thingClass);
				return listServiceDefinition(uiRowId.getValue(), "T", _class);
			} catch (Exception e) {
				throw new XException(e);
			}
		}
		List<Object> result = new ArrayList<>();

		/**
		 * 显示当前ThingModel中动态的定义的ServiceDefinition服务
		 */
		XThingModel thingModel = PersistenceHelper.service().refresh(new ObjectReference<XThingModel>(oid));
		if (thingModel != null) {
			Map<String, Object> tmodelData = new HashMap<String, Object>();
			tmodelData.put("oid", thingModel.getOid());
			tmodelData.put("name", "<img src='" + thingModel.getIcon() + "'/>" + thingModel.getName());
			tmodelData.put("state", "open");
			result.add(tmodelData);

			List<Map<String, Object>> subSet = listServiceDefinition(thingModel);
			tmodelData.put("children", subSet);
		}
		return result;
	}

	private List<Map<String, Object>> getChildHierarchy(XThingModel thingModel) {
		List<Map<String, Object>> result = new ArrayList<>();

		//	XThingModel baseModel = thingModel.getBaseModel();
		//	if (baseModel != null) {
		//		Map<String, Object> baseData = new HashMap<String, Object>();
		//		baseData.put("oid", "M:" + thingModel.getOid() + "~" + baseModel.getOid());
		//		baseData.put("name", "<img src='" + baseModel.getIcon() + "'/>" + baseModel.getName());
		//		baseData.put("state", "open");
		//		result.add(baseData);
		//	}
		Class<?> entityClass = thingModel.getEntityClass();
		Class<?> modelClass = thingModel.getModelClass();
		if (modelClass != null) {
			Map<String, Object> packData = new HashMap<String, Object>();
			packData.put("oid", thingModel.getOid() + "~" + thingModel.getModel());
			packData.put("name", "<img src='images/services.png'/>" + thingModel.getModelClass().getSimpleName());
			packData.put("state", "closed");
			result.add(packData);
		}

		XThingModel baseModel = (XThingModel) thingModel.getThingModel();
		if (baseModel != null) {
			Map<String, Object> baseData = new HashMap<String, Object>();
			baseData.put("oid", "M-" + thingModel.getOid() + "-" + baseModel.getOid());
			baseData.put("name", "<img src='images/tmodel.png'/>" + baseModel.getName());
			baseData.put("state", "closed");
			result.add(baseData);
		}

		//	for (Entry<String, String> entry : getMethodDisplay(entityClass).entrySet()) {
		//		Map<String, Object> nativeData = new HashMap<String, Object>();
		//		nativeData.put("oid", thingModel.getOid() + "~" + entry.getKey());
		//		nativeData.put("name", "<img src='images/service_local.png'/> " + entry.getValue());
		//		nativeData.put("state", "open");
		//		result.add(nativeData);
		//	}

		return result;
	}

	private List<Map<String, Object>> listServiceDefinition(XThingModel thingModel) {
		List<Map<String, Object>> result = new ArrayList<>();

		Class<?> modelClass = thingModel.getModelClass();
		if (modelClass != null) {
			Map<String, Object> packData = new HashMap<String, Object>();
			packData.put("oid", "T-" + thingModel.getOid() + "~" + thingModel.getModel());
			packData.put("name", "<img src='images/services.png'/>" + thingModel.getModelClass().getSimpleName());
			packData.put("state", "closed");
			result.add(packData);
		}

		List<IServiceDefinition> serviceList = thingModel.getServiceDefinitions();// tmodelService.listServiceDefinition(thingModel);
		for (IServiceDefinition serviceDef : serviceList) {
			Map<String, Object> serviceData = new HashMap<String, Object>();
			serviceData.put("oid", "S-" + thingModel.getOid() + "-" + serviceDef.getOid());
			serviceData.put("name", "<img src='" + serviceDef.getServiceType().getIcon() + "'/> " + getServiceDisplay(serviceDef));
			serviceData.put("state", "open");
			result.add(serviceData);
		}

		/**
		 * 显示当前XThingModel对应的Entity的方法
		 */
		result.addAll(listServiceDefinition(thingModel.getOid(), "C", thingModel.getEntityClass()));

		return result;
	}

	private List<Map<String, Object>> listServiceDefinition(String prefix, String clsType, Class<?> clazz) {
		Method[] methods = clazz.getDeclaredMethods();

		/**
		 * 构建Service的显示“query(String sql):InfoTable”
		 */
		Map<String, String> sortMap = new TreeMap<String, String>();
		for (Method method : methods) {
			if (!Modifier.isPublic(method.getModifiers()))
				continue;

			Map<String, String> methodDisplay = getMethodDisplay(method);
			if (methodDisplay.isEmpty())
				continue;

			for (Entry<String, String> entry : methodDisplay.entrySet()) {
				if (!sortMap.containsKey(entry.getKey())) {
					sortMap.put(entry.getKey(), entry.getValue());
				}
			}
		}

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Entry<String, String> entry : sortMap.entrySet()) {
			Map<String, Object> localService = new HashMap<String, Object>();
			localService.put("oid", entry.getKey());
			if ("C".equals(clsType)) {
				localService.put("name", "<img src='images/config_local.png'/> " + entry.getValue());
			} else if ("T".equals(clsType)) {
				localService.put("name", "<img src='images/service_local.png'/> " + entry.getValue());
			}
			localService.put("state", "open");
			result.add(localService);
		}

		Class<?> superCls = clazz.getSuperclass();
		if (!"Object".equals(superCls.getSimpleName())) {
			Map<String, Object> superMap = new HashMap<String, Object>();
			superMap.put("oid", prefix + "~" + clsType + ":" + superCls.getName());
			if ("M".equals(clsType)) {
				superMap.put("name", "<img src='images/tmodel.png'/>" + superCls.getSimpleName());
			} else if ("C".equals(clsType)) {
				superMap.put("name", "<img src='images/configs.png'/>" + superCls.getSimpleName());
			} else if ("T".equals(clsType)) {
				superMap.put("name", "<img src='images/services.png'/>" + superCls.getSimpleName());
			}
			superMap.put("state", "closed");
			result.add(superMap);
		}

		return result;
	}

	private String getServiceDisplay(IServiceDefinition serviceDef) {
		StringBuffer serviceBuf = new StringBuffer(serviceDef.getName() + "(");
		List<Argument> arguments = serviceDef.getArguments();
		int j = 0;
		for (int i = 0; i < arguments.size(); i++) {
			Argument argument = arguments.get(i);
			if (j++ == 0) {
				serviceBuf.append(argument.getBaseType().getDisplay());
			} else {
				serviceBuf.append(", ").append(argument.getBaseType().getDisplay());
			}
		}
		serviceBuf.append(")");
		return serviceBuf.toString() + " : " + serviceDef.getResultType().getDisplay();
	}

	private Map<String, String> getMethodDisplay(Class<?> entityClass) {
		Map<String, String> sortMap = new TreeMap<String, String>();
		if (entityClass == null)
			return sortMap;

		Class<?> clazz = entityClass;
		while (clazz != null) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (!Modifier.isPublic(method.getModifiers()))
					continue;

				Map<String, String> methodDisplay = getMethodDisplay(method);
				if (methodDisplay.isEmpty())
					continue;

				for (Entry<String, String> entry : methodDisplay.entrySet()) {
					if (!sortMap.containsKey(entry.getKey())) {
						sortMap.put(entry.getKey(), entry.getValue());
					}
				}
			}

			clazz = clazz.getSuperclass();

			if (Object.class.equals(clazz))
				break;
		}

		return sortMap;
	}

	private Map<String, String> getMethodDisplay(Method method) {
		Map<String, String> result = new HashMap<>();
		if (method == null)
			return result;

		/**
		 * jQuery会对特殊字符“.”、“#”、“(”、“[”进检查，因此id中不能够包含这些字符
		 */
		StringBuffer methodKey = new StringBuffer(method.getName());
		StringBuffer methodDef = new StringBuffer(method.getName()).append("(");

		Parameter[] params = method.getParameters();
		for (int i = 0; i < params.length; i++) {
			Parameter param = params[i];
			Class<?> type = param.getType();
			if (i == 0) {
				methodKey.append("-").append(type.getSimpleName());
				methodDef.append(type.getSimpleName()).append(" ").append(param.getName());
			} else {
				methodKey.append("-").append(type.getSimpleName());
				methodDef.append(",").append(type.getSimpleName()).append(" ").append(param.getName());
			}
		}

		methodDef.append(")");
		result.put(methodKey.toString(), methodDef.toString());

		return result;
	}
}
