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
import com.flame.orm.XTransaction;
import com.flame.thing.Argument;
import com.flame.thing.XServiceType;
import com.flame.type.XBaseType;
import com.flame.util.JsonUtils;
import com.flame.util.XException;
import com.thing.ThingPerformHelper;
import com.thing.ThingUtilities;
import com.thing.entity.XServiceDefinition;

public class EditServiceDefProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		String serviceName = commandBean.getTextParameter("name");
		if (!ThingUtilities.checkVarFormat(serviceName)) {
			throw new XException("Format error in name property!");
		}
		XTransaction trans = new XTransaction();
		try {
			trans.begin();
			XServiceDefinition serviceDef = (XServiceDefinition) commandBean.getPrimaryObj();
			serviceDef.setName(serviceName);
			serviceDef.setDescription(commandBean.getTextParameter("description"));
			serviceDef.setServiceType(XServiceType.valueOf(commandBean.getTextParameter("serviceType")));
			serviceDef.setResultType(XBaseType.valueOf(commandBean.getTextParameter("resultType")));

			Map<String, Argument> paramMap = new HashMap<>();
			for (Argument param : serviceDef.getArguments()) {
				paramMap.put(param.getName(), param);
			}

			String arguments = commandBean.getTextParameter("arguments");
			if (arguments != null && !"".equals(arguments)) {
				List<Argument> argumentList = new ArrayList<Argument>();
				ArrayNode arrayNode = (ArrayNode) JsonUtils.parseNode(arguments);
				Iterator<JsonNode> it = arrayNode.elements();
				while (it.hasNext()) {
					JsonNode node = it.next();
					String name = node.get("name").asText();
					String description = node.get("description").asText();
					String type = node.get("type").asText();

					Argument _argument = paramMap.get(name);
					if (_argument == null) {
						_argument = new Argument();
					} else {
						paramMap.remove(name);
					}
					_argument.setName(name);
					_argument.setBaseType(XBaseType.valueOf(type));
					_argument.setDescription(description);
					argumentList.add(_argument);
				}
				serviceDef.setArguments(argumentList);
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

			trans.commit();
			trans = null;
		} finally {
			if (trans != null) {
				trans.rollback();
			}
		}

		return formResult;
	}
}
