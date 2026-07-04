package com.flame.orm;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.flame.util.JsonUtils;
import com.flame.util.XException;

import jakarta.persistence.AttributeConverter;

public class JsonObjectConverter implements AttributeConverter<Object, String> {
	@Override
	public String convertToDatabaseColumn(Object object) {
		if (object == null)
			return null;

		return JsonUtils.toJson(object);
	}

	@Override
	public Object convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.trim().isBlank())
			return null;

        JsonNode node = JsonUtils.parseNode(dbData);
        if (!node.has(XConstant.XCLASS))
            return JsonUtils.toObject(dbData, Object.class);

        String xclass = node.get(XConstant.XCLASS).asText();
        if (StringUtils.isBlank(xclass))
            return JsonUtils.toObject(dbData, Object.class);

        try {
            Class<?> clazz = Class.forName(xclass);
            return JsonUtils.toObject(node, clazz);
        } catch (ClassNotFoundException e) {
            throw new XException(e);
        }
	}
}
