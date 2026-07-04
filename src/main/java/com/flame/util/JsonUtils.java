package com.flame.util;

import com.fasterxml.jackson.core.JsonParser.Feature;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        /**
         * JSON格式可以使用单引号
         */
        objectMapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        /**
         * -转换为格式化的json mapper.enable(SerializationFeature.INDENT_OUTPUT);
         */

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        /**
         * -如果json中有新增的字段并且是实体类类中不存在的，不报错
         */
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        /**
         * DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES忽略多余的字段
         */
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJsonString(Object object) {
        if (object == null)
            return "";
        
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new com.flame.util.XException(e);
        }
    }

    /**
     * 将对象转换为格式化的JSON字符串（带缩进，便于阅读）。
     *
     * <p>使用默认的缩进格式（2空格）输出JSON，适合日志打印、调试等场景。</p>
     *
     * @param object 要转换的对象，可以是Java对象、Map、List、JsonNode等
     * @return 格式化的JSON字符串，如果对象为null则返回空字符串
     * @throws XException 如果转换过程中发生错误
     */
    public static String prettyJsonString(Object object) {
        if (object == null)
            return "";

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static JsonNode convertJsonNode(String json) {
        if (json == null)
            return null;

        try {
            return objectMapper.readValue(json, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new com.flame.util.XException(e);
        }
    }

    public static <T> T convertT(String json, Class<T> clazz) {
        if (json == null)
            return null;

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static <T> T convertT(JsonNode node, Class<T> clazz) {
        if (node == null)
            return null;

        return objectMapper.convertValue(node, clazz);
    }

    public static JsonNode parseNode(String json) {
        if (json == null || json.trim().isBlank())
            return null;

        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static List<?> parseAsList(String json) {
        if (json == null || json.trim().isBlank())
            return null;

        try {
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static Map<?, ?> parseAsMap(String json) {
        if (json == null || json.trim().isBlank())
            return null;

        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static String toJson(Object object) {
        if (object == null)
            return "";

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        if (json == null || json.trim().isBlank())
            return null;

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static <T> T toObject(JsonNode node, Class<T> clazz) {
        if (node == null)
            return null;

        return objectMapper.convertValue(node, clazz);
    }

    public static <T> List<T> parseArray(String json, Class<T> elementClass) {
        if (json == null || json.trim().isBlank())
            return null;

        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static <T> List<T> parseArray(String json, TypeReference<List<T>> typeRef) {
        if (json == null || json.trim().isBlank())
            return null;

        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

}
