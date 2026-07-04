package com.flame.thing;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flame.orm.XJsonType;
import com.flame.type.XBaseType;
import com.flame.util.JsonUtils;
import com.flame.util.XException;

import jakarta.persistence.AttributeConverter;

/**
 * @author ph
 * @version 1.0
 * @created 29-10月-2019 22:20:04
 */
public class Argument extends XJsonType<Argument> {
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private XBaseType baseType;

	public Argument() {
	}

	public Argument(String name, String description, XBaseType baseType) {
		this.name = name;
		this.description = description;
		this.baseType = baseType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public XBaseType getBaseType() {
		return baseType;
	}

	public void setBaseType(XBaseType baseType) {
		this.baseType = baseType;
	}
	
	public class MapConverter implements AttributeConverter<Map<String, Argument>, String>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public String convertToDatabaseColumn(Map<String, Argument> attribute) {
			return JsonUtils.toJson(attribute);
		}

		@Override
		public Map<String, Argument> convertToEntityAttribute(String dbData) {
			if (dbData == null || dbData.trim().isBlank())
				return Collections.emptyMap();

			try {
				JsonNode jsonNode = JsonUtils.parseNode(dbData);
				if (jsonNode instanceof ArrayNode) {
					ArrayNode arrayNode = (ArrayNode) jsonNode;
					Map<String, Argument> result = new HashMap<>();
					for (JsonNode node : arrayNode) {
						Argument argument = JsonUtils.toObject(node, Argument.class);
						if (argument != null) {
							argument.postProcess();
							result.put(argument.getName(), argument);
						}
					}

					return result;
				} else {
					throw new XException("JSON无法转换成Map对象.");
				}
			} catch (Exception e) {
				throw new XException(e);
			}
		}

	}
}