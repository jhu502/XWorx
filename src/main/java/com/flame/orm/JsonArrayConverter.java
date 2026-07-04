package com.flame.orm;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flame.util.JsonUtils;
import com.flame.util.XException;
import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonArrayConverter<T> implements AttributeConverter<List<T>, String>, Serializable {
    private static final long serialVersionUID = 1L;
    
    @Override
    public String convertToDatabaseColumn(List<T> attribute) {
        return JsonUtils.toJson(attribute);
    }

    @Override
    public List<T> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isBlank())
            return Collections.emptyList();

        try {
            JsonNode jsonNode = JsonUtils.parseNode(dbData);
            if (jsonNode instanceof ArrayNode) {
                /**
                 * 将ArrayNode对象转换成List对象
                 */
                ArrayNode arrayNode = (ArrayNode) jsonNode;
                List<T> resultList = new ArrayList<>();
                for (JsonNode node : arrayNode) {
                    if (!node.has(XConstant.XCLASS))
                        continue;

                    String xclass = node.get(XConstant.XCLASS).asText();
                    if (StringUtils.isBlank(xclass))
                        continue;

                    Class<?> clazz = Class.forName(xclass);
					@SuppressWarnings("unchecked")
                    T t = (T) JsonUtils.toObject(node, clazz);
                    if (t != null) {
                        resultList.add(t);
                    }
                }
                return resultList;
            } else {
                throw new XException("JSON无法转换成List对象.");
            }
        } catch (Exception e) {
            throw new XException(e);
        }
    }
}
