package com.flame.rpc;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flame.annotations.XParam;
import com.flame.type.IPrimitiveType;
import com.flame.type.XBaseType;
import com.flame.util.XException;

public class FlameRPCFactory {
    private static final Logger logger = LoggerFactory.getLogger(FlameRPCFactory.class);
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String getBase64Encode(String value) {
        if (value == null) {
            return null;
        }
        return new String(Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    public static String getBase64Decode(String value) {
        if (value == null) {
            return null;
        }
        return new String(Base64.getDecoder().decode((String) value), StandardCharsets.UTF_8);
    }

    public static boolean isValidBase64(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        try {
            Base64.getDecoder().decode(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static Object decodeParameter(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> param = (Map<Object, Object>) object;
            for (Entry<?, ?> entry : param.entrySet().toArray(new Entry[]{})) {
                Object value = entry.getValue();
                if (value instanceof String && isValidBase64((String) value)) {
                    param.put(entry.getKey(), getBase64Decode((String) value));
                } else {
                    decodeParameter(value);
                }
            }
        } else if (object instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) object;
            Object[] objs = list.toArray();
            for (int i = 0; i < objs.length; i++) {
                Object value = objs[i];
                if (value instanceof String && isValidBase64((String) value)) {
                    list.set(i, getBase64Decode((String) value));
                } else {
                    decodeParameter(value);
                }
            }
        } else if (object instanceof FlameResult) {
            FlameResult flameResult = (FlameResult) object;
            FlameResult.Error error = flameResult.getError();
            if (error != null) {
                String message = error.getMessage();
                if (message != null) {
                    if (isValidBase64(message)) {
                        error.setMessage(getBase64Decode(message));
                    } else {
                        error.setMessage(message);
                    }
                }
                Object data = error.getData();
                if (data instanceof String) {
                    if (isValidBase64((String) data)) {
                        error.setData(getBase64Decode((String) data));
                    } else {
                        error.setData(data);
                    }
                } else if (data instanceof List || data instanceof Map) {
                    decodeObject(data);
                }
            }
            Object result = flameResult.getResult();
            if (result instanceof String) {
                if (isValidBase64((String) result)) {
                    flameResult.setResult(getBase64Decode((String) result));
                } else {
                    flameResult.setResult(result);
                }
            } else if (result instanceof List || result instanceof Map) {
                decodeObject(result);
            }
        }

        return object;
    }

    public static Object decodeRPC(String rawMessage) {
        try {
            JsonNode jsonNode = mapper.readTree(rawMessage);

            if (jsonNode.isArray()) {
                return decodeBatch(rawMessage);
            }

            if (!jsonNode.has(FlameRPC.JSONRPC) || !jsonNode.get(FlameRPC.JSONRPC).asText().equals("2.0")) {
                FlameResult error = FlameResult.invalidRequest("Invalid JSON-RPC version");
                if (jsonNode.has(FlameRPC.ID)) {
                    error.setId(jsonNode.get(FlameRPC.ID).asText());
                }
                return error;
            }

            if (jsonNode.has(FlameRPC.METHOD)) {
                return decodeRequest(jsonNode);
            } else if (jsonNode.has(FlameRPC.RESULT) || jsonNode.has(FlameRPC.ERROR)) {
                return decodeResponse(jsonNode);
            } else {
                return FlameResult.invalidRequest("Missing method, result, or error");
            }
        } catch (JsonProcessingException e) {
            FlameResult error = FlameResult.parseError("Parse error: " + e.getMessage());
            return error;
        } catch (Exception e) {
            FlameResult error = FlameResult.internalError("Internal error: " + e.getMessage());
            return error;
        }
    }

    private static FlameRPC decodeRequest(JsonNode jsonNode) {
        if (jsonNode == null)
            return null;

        FlameMessage flameMessage = new FlameMessage();

        if (jsonNode.has(FlameRPC.ID)) {
            JsonNode idNode = jsonNode.get(FlameRPC.ID);
            if (!idNode.isNull()) {
                if (idNode.isIntegralNumber()) {
                    flameMessage.setId(idNode.asLong());
                } else {
                    flameMessage.setId(idNode.asText());
                }
            }
        }

        flameMessage.method = jsonNode.has(FlameRPC.METHOD) ? jsonNode.get(FlameRPC.METHOD).asText() : "";
        flameMessage.thing = jsonNode.has(FlameRPC.THING) ? jsonNode.get(FlameRPC.THING).asText() : "";

        if (jsonNode.has(FlameRPC.PARAMS)) {
            JsonNode paramsNode = jsonNode.get(FlameRPC.PARAMS);
            if (paramsNode.isArray()) {
                List<Object> positionalParams = mapper.convertValue(paramsNode, new TypeReference<>() {});
                decodeParameter(positionalParams);
                flameMessage.setPositionalParams(positionalParams);
            } else if (paramsNode.isObject()) {
                Map<String, Object> params = mapper.convertValue(paramsNode, new TypeReference<>() {});
                for (Entry<String, Object> entry : params.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        String strValue = (String) value;
                        IPrimitiveType<?> primitive = XBaseType.decodeToPrimitive(strValue);
                        if (primitive != null && primitive.getValue() != null) {
                            params.put(entry.getKey(), primitive.getValue());
                        }
                    }
                }
                flameMessage.setParams(params);
            }
        }

        return flameMessage;
    }

    private static FlameRPC decodeResponse(JsonNode jsonNode) {
        FlameResult result = new FlameResult();

        if (jsonNode.has(FlameRPC.ID)) {
            JsonNode idNode = jsonNode.get(FlameRPC.ID);
            if (!idNode.isNull()) {
                if (idNode.isIntegralNumber()) {
                    result.setId(idNode.asLong());
                } else {
                    result.setId(idNode.asText());
                }
            }
        }

        if (jsonNode.has(FlameRPC.ERROR) && !jsonNode.get(FlameRPC.ERROR).isNull()) {
            JsonNode errorNode = jsonNode.get(FlameRPC.ERROR);
            FlameResult.Error error = new FlameResult.Error();
            if (errorNode.has("code")) {
                error.setCode(errorNode.get("code").asInt());
            }
            if (errorNode.has(FlameRPC.METHOD)) {
                error.setMessage(errorNode.get(FlameRPC.MESSAGE).asText());
            }
            if (errorNode.has("data")) {
                try {
                    error.setData(mapper.treeToValue(errorNode.get("data"), Object.class));
                } catch (JsonProcessingException e) {
                    error.setData(errorNode.get("data").toString());
                }
            }
            result.setError(error);
        } else if (jsonNode.has(FlameRPC.RESULT)) {
            try {
                result.setResult(mapper.treeToValue(jsonNode.get(FlameRPC.RESULT), Object.class));
            } catch (JsonProcessingException e) {
                result.setResult(jsonNode.get(FlameRPC.RESULT).toString());
            }
        }

        decodeParameter(result);
        return result;
    }

    private static List<FlameRPC> decodeBatch(String rawMessage) {
        try {
            List<JsonNode> nodes = mapper.readValue(rawMessage, new TypeReference<>() {});
            List<FlameRPC> results = new ArrayList<>();
            for (JsonNode node : nodes) {
                String nodeString = node.toString();
                Object rpcObject = decodeRPC(nodeString);
                if (rpcObject instanceof FlameRPC) {
                    results.add((FlameRPC) rpcObject);
                }
            }
            return results;
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static String encodeRPC(FlameRPC rpc) {
        if (rpc == null) {
            return "";
        }

        if (rpc instanceof List) {
            return encodeBatch((List<FlameRPC>) rpc);
        }

        if (rpc instanceof FlameMessage) {
            FlameMessage flameMessage = (FlameMessage) rpc;
            return encodeMessage(flameMessage);
        } else if (rpc instanceof FlameResult) {
            FlameResult flameResult = (FlameResult) rpc;
            return encodeResult(flameResult);
        } else {
            return "";
        }
    }

    private static String encodeBatch(List<FlameRPC> rpcs) {
        ArrayNode array = mapper.createArrayNode();
        for (FlameRPC rpc : rpcs) {
            try {
                if (rpc instanceof FlameMessage) {
                    array.add(mapper.readTree(encodeMessage((FlameMessage) rpc)));
                } else if (rpc instanceof FlameResult) {
                    array.add(mapper.readTree(encodeResult((FlameResult) rpc)));
                }
            } catch (JsonProcessingException e) {
                logger.warn("Failed to encode batch item: {}", e.getMessage());
            }
        }
        return array.toString();
    }

    public static String encodeMessage(FlameMessage message) {
        ObjectNode jsonObj = mapper.createObjectNode();

        jsonObj.put(FlameRPC.JSONRPC, FlameMessage.JSONRPC_VERSION);

        if (!message.isNotification()) {
            if (message.getId() != null) {
                Number numId = message.getNumericId();
                if (numId != null) {
                    jsonObj.putPOJO("id", numId);
                } else {
                    jsonObj.put("id", message.getId());
                }
            }
        }

        jsonObj.put(FlameRPC.METHOD, message.getFullMethod());

        if (message.isPositionalParams() && message.getPositionalParams() != null) {
            ArrayNode params = encodeArray(message.getPositionalParams());
            jsonObj.set(FlameRPC.PARAMS, params);
        } else if (message.getParams() != null) {
            ObjectNode params = encodeObject(message.getParams());
            jsonObj.set(FlameRPC.PARAMS, params);
        }

        return jsonObj.toString();
    }

    public static String encodeResult(FlameResult result) {
        ObjectNode jsonObj = mapper.createObjectNode();

        jsonObj.put(FlameRPC.JSONRPC, FlameMessage.JSONRPC_VERSION);

        if (!result.isNotification()) {
            if (result.getId() != null) {
                Number numId = result.getNumericId();
                if (numId != null) {
                    jsonObj.putPOJO(FlameRPC.ID, numId);
                } else {
                    jsonObj.put(FlameRPC.ID, result.getId());
                }
            }
        }

        FlameResult.Error error = result.getError();
        if (error != null) {
            ObjectNode errorObj = mapper.createObjectNode();
            errorObj.putPOJO("code", error.getCode());
            errorObj.put(FlameRPC.MESSAGE, error.getMessage());
            if (error.getData() != null) {
                errorObj.putPOJO("data", error.getData());
            }
            jsonObj.set("error", errorObj);
        }

        if (result.getResult() != null) {
            Object object = result.getResult();
            if (object instanceof List) {
                jsonObj.set(FlameRPC.RESULT, encodeArray(object));
            } else if (object instanceof Map) {
                jsonObj.set(FlameRPC.RESULT, encodeObject(object));
            } else if (object instanceof Number || object instanceof Boolean) {
                jsonObj.putPOJO(FlameRPC.RESULT, object);
            } else {
                jsonObj.put(FlameRPC.RESULT, object != null ? object.toString() : "");
            }
        }

        return jsonObj.toString();
    }

    private static ObjectNode encodeObject(Object object) {
        ObjectNode jsonObj = mapper.createObjectNode();
        if (object == null) {
            return jsonObj;
        }
        if (object instanceof Map) {
            for (Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
                Object key = entry.getKey();
                Object val = entry.getValue();

                if (val == null) {
                    jsonObj.putNull((String) key);
                } else if (val instanceof String) {
                    jsonObj.put((String) key, (String) val);
                } else if (val instanceof Integer) {
                    jsonObj.put((String) key, (Integer) val);
                } else if (val instanceof Double) {
                    jsonObj.put((String) key, (Double) val);
                } else if (val instanceof Float) {
                    jsonObj.put((String) key, (Float) val);
                } else if (val instanceof Long) {
                    jsonObj.put((String) key, (Long) val);
                } else if (val instanceof Boolean) {
                    jsonObj.put((String) key, (Boolean) val);
                } else if (val instanceof Number) {
                    jsonObj.putPOJO((String) key, val);
                } else if (val instanceof Object[] || val instanceof List) {
                    jsonObj.set((String) key, encodeArray(val));
                } else if (val instanceof Map) {
                    jsonObj.set((String) key, encodeObject(val));
                } else {
                    jsonObj.putPOJO((String) key, val);
                }
            }
        }

        return jsonObj;
    }

    private static void decodeObject(Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) object;
            int length = list.size();
            for (int i = 0; i < length; i++) {
                Object val = list.get(i);
                if (val instanceof String && isValidBase64((String) val)) {
                    list.set(i, getBase64Decode((String) val));
                } else {
                    decodeObject(val);
                }
            }
        } else if (object instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) object;
            for (Entry<?, ?> entry : map.entrySet()) {
                String key = (String) entry.getKey();
                Object val = entry.getValue();
                if (val instanceof String && isValidBase64((String) val)) {
                    map.put(key, getBase64Decode((String) val));
                } else {
                    decodeObject(val);
                }
            }
        }
    }

    private static ArrayNode encodeArray(Object object) {
        ArrayNode array = mapper.createArrayNode();
        if (object == null) {
            return array;
        }
        if (object instanceof List) {
            for (Object obj : (List<?>) object) {
                array.addPOJO(encodeArrayElement(obj));
            }
        } else if (object instanceof Object[]) {
            for (Object obj : (Object[]) object) {
                array.addPOJO(encodeArrayElement(obj));
            }
        }
        return array;
    }

    private static Object encodeArrayElement(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return obj;
        } else if (obj instanceof Integer) {
            return obj;
        } else if (obj instanceof Double) {
            return obj;
        } else if (obj instanceof Float) {
            return obj;
        } else if (obj instanceof Long) {
            return obj;
        } else if (obj instanceof Boolean) {
            return obj;
        } else if (obj instanceof Number) {
            return obj;
        } else if (obj instanceof Object[] || obj instanceof List) {
            return encodeArray(obj);
        } else if (obj instanceof Map) {
            return encodeObject(obj);
        } else {
            return obj.toString();
        }
    }

    public static FlameMessage genFlameMessage(String id, String thing, String method, XParam[] params, Object[] arguments) {
        FlameMessage message = new FlameMessage();
        message.setId(id);
        message.setThing(thing);
        message.setMethod(method);

        Map<String, Object> _params = new LinkedHashMap<String, Object>(params.length);
        for (int i = 0; i < params.length; i++) {
            XParam param = params[i];
            IPrimitiveType<?> primitive = param.type().getPrimitive(arguments[i]);
            _params.put(param.name(), primitive.getEncode());
        }
        message.setParams(_params);

        return message;
    }

    public static FlameMessage createRequest(String method, Map<String, Object> params) {
        FlameMessage message = new FlameMessage();
        message.setId(String.valueOf(System.currentTimeMillis()));
        message.setMethod(method);
        message.setParams(params);
        return message;
    }

    public static FlameMessage createNotification(String method, Map<String, Object> params) {
        FlameMessage message = new FlameMessage();
        message.setMethod(method);
        message.setParams(params);
        return message;
    }

    public static FlameMessage createNotification(String method, List<Object> params) {
        FlameMessage message = new FlameMessage();
        message.setMethod(method);
        message.setPositionalParams(params);
        return message;
    }

    public static FlameResult genFlameResult(FlameMessage message, Object result) {
        FlameResult fResult = new FlameResult();
        if (message != null) {
            fResult.id = message.id;
        }

        if (result instanceof Value) {
            Value value = (Value) result;
            if (value.isString())
                fResult.setResult(value.asString());
            else if (value.isBoolean())
                fResult.setResult(value.asBoolean());
            else if (value.isDate())
                fResult.setResult(value.asDate());
            else if (value.isDuration())
                fResult.setResult(value.asDuration());
            else if (value.isHostObject())
                fResult.setResult(value.asHostObject());
            else if (value.isInstant())
                fResult.setResult(value.asInstant());
            else if (value.isNativePointer())
                fResult.setResult(value.asNativePointer());
            else if (value.isNull())
                fResult.setResult(null);
            else if (value.isNumber())
                fResult.setResult(value.asDouble());
            else if (value.isProxyObject())
                fResult.setResult(value.asProxyObject());
            else if (value.isTime())
                fResult.setResult(value.asTime());
            else if (value.isTimeZone())
                fResult.setResult(value.asTimeZone());
        } else {
            fResult.setResult(result);
        }

        return fResult;
    }

    public static FlameResult genFlameResult(FlameMessage message, Exception e) {
        FlameResult fResult = new FlameResult();
        if (message == null) {
            fResult.setException(e);
            return fResult;
        }

        fResult.id = message.id;

        if (e instanceof InvocationTargetException) {
            logger.error(message.toJsonString(), ((InvocationTargetException) e).getCause());
            fResult.setException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException) {
            logger.error(message.toJsonString(), e);
            fResult.setException(new XException("Method:" + message.getMethod() + " not found."));
        } else {
            logger.error(message.toJsonString(), e);
            fResult.setException(e);
        }

        return fResult;
    }

    public static FlameResult genFlameResult(FlameMessage message, int errorCode, String errorMessage) {
        FlameResult fResult = new FlameResult();
        if (message != null) {
            fResult.id = message.id;
        }
        fResult.setError(errorCode, errorMessage);
        return fResult;
    }

    public static String toJsonString(FlameRPC flamerpc) {
        try {
            return mapper.writeValueAsString(flamerpc);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static <T extends FlameRPC> T parse(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new XException(e);
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        String msg_ret = "{'id':'1','jsonrpc':'2.0','error': {'code':-32600,'message':'Invalid Request'},'result':null}";
        Object result = FlameRPCFactory.decodeRPC(msg_ret);
        if (result instanceof FlameRPC) {
            System.out.println("Parse Response: " + FlameRPCFactory.toJsonString((FlameRPC) result));
        }

        String msg_map = "{'jsonrpc':'2.0','method':'XUser.Guest.sendMessage','params':{'msg':'Hello','type':1}}";
        Object message = FlameRPCFactory.decodeRPC(msg_map);
        if (message instanceof FlameRPC) {
            System.out.println("Parse Request: " + ((FlameRPC) message).toJsonString());
        }

        FlameMessage request = new FlameMessage("1", "XUser", "login");
        request.setParams(Map.of("username", "admin", "token", "abc123"));
        System.out.println("Encode Request: " + encodeRPC(request));

        FlameMessage notification = new FlameMessage();
        notification.setMethod("broadcast.message");
        notification.setParams(Map.of("msg", "Hello All"));
        System.out.println("Encode Notification: " + encodeRPC(notification));

        FlameResult response = FlameResult.ok("Login successful");
        response.setId("1");
        System.out.println("Encode Response: " + encodeRPC(response));

        FlameResult errorResponse = FlameResult.methodNotFound("Method not found");
        errorResponse.setId("2");
        System.out.println("Encode Error: " + encodeRPC(errorResponse));
    }
}
