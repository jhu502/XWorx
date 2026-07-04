package com.thing.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XPersistable;
import com.flame.thing.Argument;
import com.flame.thing.IModelManaged;
import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IThingModel;
import com.flame.type.XBaseType;
import com.thing.ThingEntityHelper;
import com.thing.ThingPerformHelper;
import com.thing.ThingUtilities;
import com.thing.common.IBindable;
import com.thing.common.IConnectable;
import com.thing.common.IThingManaged;
import com.thing.entity.ModeledEntity;
import com.thing.entity.XServiceDefinition;
import com.thing.entity.XThingModel;
import com.thing.service.StandardModelManager;

import io.netty.util.internal.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;

@RestController
@RequestMapping(value = "/XWorx/TModelController", produces = MediaType.APPLICATION_JSON_VALUE) //IE收到APPLICATION_JSON_VALUE返回json时，会提示下载json文件
public class TModelController {
	@Resource
	private StandardModelManager thingmodelManager;

	@Operation(summary = "注册ThingModel API", parameters = { @Parameter(name = "className", description = "Meta Class Name", required = true) })
	@RequestMapping(value = "/registerThingModel", method = RequestMethod.GET)
	public String registerThingModel(String className) throws ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<? extends ModeledEntity> meta = (Class<? extends ModeledEntity>) Class.forName(className);
		XThingModel thingModel = (XThingModel) thingmodelManager.registerThingModel(meta);

		return thingModel.getOid();
	}

	@Operation(summary = "根据给定的参数，返回所有相关的Thing", parameters = { @Parameter(name = "oid", required = true), @Parameter(name = "field", required = true), @Parameter(name = "value", required = true) })
	@RequestMapping(value = "/queryThingEntitys", method = RequestMethod.GET)
	public List<?> queryThingEntitys(String oid, String field, String value) {
		if (oid == null || "".equals(oid.trim()))
			return Collections.EMPTY_LIST;

		if (ThingUtilities.checkEntityIdentity(oid)) {
			XThingModel model = PersistenceHelper.service().refresh(new ObjectReference<XThingModel>(oid));
			return ThingEntityHelper.service().queryConfigEntity(model, field, value);
		}

		return Collections.EMPTY_LIST;
	}

	@RequestMapping(value = "/listBaseType", method = RequestMethod.GET)
	public List<Map<String, String>> listFieldType() {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (XBaseType ftype : XBaseType.values()) {
			Map<String, String> _row = new HashMap<String, String>();
			_row.put("type", ftype.name());
			_row.put("display", ftype.getDisplay());
			result.add(_row);
		}

		return result;
	}

	@Operation(summary = "显示ThingModel API", parameters = { @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "/listThingModelAPI", method = RequestMethod.GET)
	public List<Map<String, Object>> listThingModelAPI(@RequestParam("oid") String oid) {
		XThingModel thingModel = PersistenceHelper.service().refresh(new ObjectReference<XThingModel>(oid));
		Class<?> modelCls = thingModel.getEntityClass();
		return ThingUtilities.getThingConfiguration(modelCls);
	}

	@Operation(summary = "Save ServiceDefinition Code", parameters = { @Parameter(name = "oid", required = true), @Parameter(name = "code", required = true) })
	@RequestMapping(value = "/saveServiceDefCode", method = RequestMethod.POST)
	@Transactional
	public XServiceDefinition saveServiceDefCode(String oid, String code) {
		XServiceDefinition serviceDef = PersistenceHelper.service().refresh(new ObjectReference<XServiceDefinition>(oid));

		if (code != null) {
			String _code = new String(Base64.getDecoder().decode(code));
			serviceDef.setCode(_code);
			serviceDef = PersistenceHelper.service().save(serviceDef);
		}

		/**
		 * Service Definition保存后，需要刷新生成的ThingEntity
		 */
		ThingPerformHelper.service().loadServiceDefinition(serviceDef);
		return serviceDef;
	}

	@Operation(summary = "Get ServiceDefinition", parameters = { @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "/getServiceDefinition", method = RequestMethod.GET)
	public Map<String, String> getServiceDefinition(String oid) {
		Map<String, String> result = new HashMap<String, String>();
		XServiceDefinition serviceDef = PersistenceHelper.service().refresh(new ObjectReference<XServiceDefinition>(oid));
		String code = serviceDef.getCode();

		if (code != null) {
			result.put("code", new String(Base64.getEncoder().encode(code.getBytes())));
		} else {
			result.put("code", "");
		}
		XBaseType resultType = serviceDef.getResultType();
		StringBuffer function = new StringBuffer("<b style='font-style:italic;'><span style='color:#CD6600'>");
		function.append(serviceDef.getServiceType().name()).append("</span> Service: </b>");
		function.append(resultType.getDisplay()).append(" <b>").append(serviceDef.getName()).append("(</b>");
		List<Argument> arguments = serviceDef.getArguments();
		int j = 0;
		for (Argument argument : arguments) {
			if (j++ == 0) {
				function.append(argument.getBaseType().getDisplay()).append(" <span style='color:#CD6600'>").append(argument.getName()).append("</span>");
			} else {
				function.append(", ").append(argument.getBaseType().getDisplay()).append(" <span style='color:#CD6600'>").append(argument.getName()).append("</span>");
			}
		}
		function.append("<b>)</b>");
		result.put("method", function.toString());

		return result;
	}

	@Operation(summary = "Get ThingDetails", parameters = { @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "/getThingDetails", method = RequestMethod.GET)
	public Map<String, Object> getThingDetails(String oid) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isEmpty(oid))
			return result;

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		XPersistable persist = PersistenceHelper.service().find(oid);
		if (persist instanceof ModeledEntity) {
			ModeledEntity ething = (ModeledEntity) persist;

			Map<String, Object> identity = new HashMap<String, Object>();
			identity.put("name", "Identity");
			identity.put("value", ething.getThingIdentity());
			identity.put("group", "Basic Info");
			list.add(identity);

			Map<String, Object> number = new HashMap<String, Object>();
			number.put("name", "Number");
			number.put("value", ething.getNumber());
			number.put("group", "Basic Info");
			list.add(number);

			Map<String, Object> name = new HashMap<String, Object>();
			name.put("name", "Name");
			name.put("value", ething.getName());
			name.put("group", "Basic Info");
			list.add(name);

			Map<String, Object> description = new HashMap<String, Object>();
			description.put("name", "Description");
			description.put("value", ething.getDescription());
			description.put("group", "Basic Info");
			list.add(description);

			Map<String, Object> createdOn = new HashMap<String, Object>();
			createdOn.put("name", "Created On");
			createdOn.put("value", ething.getCreatedStamp());
			createdOn.put("group", "Basic Info");
			list.add(createdOn);

			Map<String, Object> lastModified = new HashMap<String, Object>();
			lastModified.put("name", "Last Modified");
			lastModified.put("value", ething.getModifiedStamp());
			lastModified.put("group", "Basic Info");
			list.add(lastModified);

			IThingManaged<IModelManaged> pthing = ThingEntityHelper.dispatch().getInflatedThingEntity(ething.getThingIdentity());
			if (pthing instanceof IConnectable) {
				Map<String, Object> connected = new HashMap<String, Object>();
				connected.put("name", "Connected");
				connected.put("value", ((IConnectable<?>) pthing).isConnected());
				connected.put("group", "State Info");
				list.add(connected);
			}

			if (pthing instanceof IBindable) {
				IBindable bindable = (IBindable) pthing;
				Map<String, Object> binded = new HashMap<String, Object>();
				binded.put("name", "Binding");
				binded.put("value", bindable.isBinding());
				binded.put("group", "State Info");
				list.add(binded);
			}

			IThingModel tmodel = ething.getThingModel();

			Map<String, Object> thingType = new HashMap<String, Object>();
			thingType.put("name", "Identity");
			thingType.put("value", tmodel.getOid());
			thingType.put("group", "Model Info");
			list.add(thingType);
		}

		result.put("total", list.size());
		result.put("rows", list);

		return result;
	}

	@Operation(summary = "Get ThingProperties", parameters = { @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "/getThingProperties", method = RequestMethod.GET)
	public List<?> getThingProperties(String oid) {
		if (StringUtil.isNullOrEmpty(oid))
			return new ArrayList<Object>();

		ModeledEntity entityThing = PersistenceHelper.service().refresh(new ObjectReference<ModeledEntity>(oid));

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		IThingModel thingModel = entityThing.getThingModel();
		if (thingModel instanceof XThingModel) {
			XThingModel xmodel = (XThingModel) thingModel;
			for (IPropertyDefinition propertyDef : xmodel.getPropertyDefinitions()) {
				Map<String, Object> row = new HashMap<String, Object>();
				row.put("oid", propertyDef.getOid());
				row.put("persistentImg", propertyDef.getPersistentImg());
				row.put("readOnlyImg", propertyDef.getReadOnlyImg());
				row.put("loggedImg", propertyDef.getLoggedImg());
				row.put("name", propertyDef.getName());
				row.put("baseType", propertyDef.getBaseType());
				row.put("description", propertyDef.getDisplay());
				row.put("defaultValue", propertyDef.getDefaultValue());
				row.put("value", "");
				row.put("nullableImg", propertyDef.getNullableImg());

				result.add(row);
			}
		}

		return result;
	}

	@RequestMapping(value = "/createThing", method = RequestMethod.POST)
	public IModelManaged createThing(String oid, @RequestParam Map<String, String> params) {
		XThingModel thgModel = (XThingModel) PersistenceHelper.service().find(oid);
		return ThingEntityHelper.service().createConfigEntity(thgModel, params);
	}

	@RequestMapping(value = "/updateThing", method = RequestMethod.POST)
	public IModelManaged updateThing(String oid, @RequestParam Map<String, String> params) {
		ModeledEntity entityThing = (ModeledEntity) PersistenceHelper.service().find(oid);
		return ThingEntityHelper.service().updateConfigEntity(entityThing, params);
	}
}
