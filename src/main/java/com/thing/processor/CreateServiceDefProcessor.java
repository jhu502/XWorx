package com.thing.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.thing.Argument;
import com.flame.thing.XServiceType;
import com.flame.type.XBaseType;
import com.flame.util.JsonUtils;
import com.flame.util.XException;
import com.thing.ThingPerformHelper;
import com.thing.ThingUtilities;
import com.thing.entity.XServiceDefinition;
import com.thing.entity.XThingModel;

public class CreateServiceDefProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		String serviceName = commandBean.getTextParameter("name");
		if (!ThingUtilities.checkVarFormat(serviceName)) {
			throw new XException("Format error in name property!");
		}
		try {
			XThingModel thingModel = (XThingModel) commandBean.getPrimaryObj();
			XServiceDefinition serviceDef = new XServiceDefinition();
			serviceDef.setName(serviceName);
			serviceDef.setDescription(commandBean.getTextParameter("description"));
			serviceDef.setServiceType(XServiceType.valueOf(commandBean.getTextParameter("serviceType")));
			serviceDef.setServiceProvider(thingModel);
			serviceDef.setResultType(XBaseType.valueOf(XBaseType.class, commandBean.getTextParameter("resultType")));

			String paramsDef = commandBean.getTextParameter("arguments");
			if (paramsDef != null && !"".equals(paramsDef)) {
				List<Argument> arguments = new ArrayList<Argument>();
				ArrayNode arrayNode = (ArrayNode) JsonUtils.parseNode(paramsDef);
				Iterator<JsonNode> its = arrayNode.elements();
				while (its.hasNext()) {
					JsonNode jnode = its.next();
					String name = jnode.get("name").asText();
					String description = jnode.get("description").asText();
					String type = jnode.get("type").asText();
					Argument paramDef = new Argument();
					paramDef.setName(name);
					paramDef.setBaseType(XBaseType.valueOf(XBaseType.class, type));
					paramDef.setDescription(description);
					arguments.add(paramDef);
				}
				serviceDef.setArguments(arguments);
			}
			serviceDef = PersistenceHelper.service().save(serviceDef);
			// Service Definition保存后，需要刷新生成的ThingEntity
			ThingPerformHelper.service().loadServiceDefinition(serviceDef);

			Map<String, Object> result = new HashMap<String, Object>();
			result.put("oid", serviceDef.getOid());
			result.put("name", serviceDef.getName());
			result.put("parameters", serviceDef.getArguments());
			result.put("serviceType", serviceDef.getServiceType());
			result.put("resultType", serviceDef.getResultType());
			result.put("code", serviceDef.getCode() == null ? "" : serviceDef.getCode());
			formResult.setData(result);
		} finally {
		}

		return formResult;
	}
}
