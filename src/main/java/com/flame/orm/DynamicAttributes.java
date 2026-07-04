package com.flame.orm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flame.util.JsonUtils;
import com.flame.util.XException;
import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

public class DynamicAttributes implements AttributeConverter<DynamicAttributes, String>, Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, XAttribute> attributes = new HashMap<>();

    public Map<String, XAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, XAttribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttributes(XAttribute value) {
        this.attributes.put(value.getName(), value);
    }

    @Override
    public String convertToDatabaseColumn(DynamicAttributes attribute) {
        if (attribute == null) {
            return "[]";
        }
        return JsonUtils.toJson(attribute.getAttributes());
    }

    @Override
    public DynamicAttributes convertToEntityAttribute(String dbData) {
		DynamicAttributes attributes = new DynamicAttributes();
		if (dbData == null || dbData.trim().isBlank()) {
			return attributes;
		}

		try {
			JsonNode jsonNode = JsonUtils.parseNode(dbData);
			if (jsonNode instanceof ArrayNode) {
				ArrayNode arrayNode = (ArrayNode) jsonNode;
				for (JsonNode node : arrayNode) {
					XAttribute attribute = JsonUtils.toObject(node, XAttribute.class);
					if (attribute != null && StringUtils.isNotBlank(attribute.getName())) {
						attribute.postProcess();
						attributes.addAttributes(attribute);
					}
				}

				return attributes;
			} else {
				XAttribute attribute = JsonUtils.toObject(dbData, XAttribute.class);
				if (attribute != null && StringUtils.isNotBlank(attribute.getName())) {
					attribute.postProcess();
					attributes.addAttributes(attribute);
				}
				return attributes;
			}
		} catch (Exception e) {
			throw new XException(e);
		}
    }
}
