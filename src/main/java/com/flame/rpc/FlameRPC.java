package com.flame.rpc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.flame.util.JsonUtils;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class FlameRPC {
    public static final String JSONRPC_VERSION = "2.0";
    public static final String JSONRPC = "jsonrpc";
    public static final String ID = "id";
    public static final String METHOD = "method";
    public static final String RESULT = "result";
    public static final String ERROR = "error";
    public static final String PARAMS = "params";
    public static final String MESSAGE = "message";
    public static final String THING = "thing";
    protected String id;

    public FlameRPC() {
        this.id = UUID.randomUUID().toString();
    }

    public FlameRPC(String id) {
        this.id = id;
    }
    
    public String getJsonrpc() {
        return JSONRPC_VERSION;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId(Number id) {
        if (id == null) {
            this.id = null;
        } else if (id instanceof Integer) {
            this.id = String.valueOf(id);
        } else if (id instanceof Long) {
            this.id = String.valueOf(id);
        } else {
            this.id = id.toString();
        }
    }

    public Number getNumericId() {
        if (id == null) {
            return null;
        }
        try {
            if (id.contains(".")) {
                return Double.parseDouble(id);
            }
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @JsonIgnore
    public boolean isNotification() {
        return id == null;
    }

    public String toJsonString() {
        return FlameRPCFactory.toJsonString(this);
    }

    public static Object parse(String json) {
        return JsonUtils.toJsonString(json);
    }

    public static String encode(FlameRPC rpc) {
        return FlameRPCFactory.encodeRPC(rpc);
    }
}
