package com.flame.rpc;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlameMessage extends FlameRPC {
    protected String thing;
    protected String method;
    protected Map<String, Object> params;
    protected List<Object> positionalParams;
    protected ParamType paramType;
    @JsonIgnore
    protected transient FlameResult result;

    public enum ParamType {
        NAMED, POSITIONAL
    }

    public FlameMessage() {
        super();
        this.paramType = ParamType.NAMED;
    }

    public FlameMessage(String id) {
        super(id);
        this.paramType = ParamType.NAMED;
    }

    public FlameMessage(String id, String thing, String method) {
        super(id);
        this.thing = thing;
        this.method = method;
        this.paramType = ParamType.NAMED;
    }

    public String getThing() {
        return thing;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }

    public void setIdentity(String identity) {
        this.thing = identity;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
        this.paramType = ParamType.NAMED;
    }

    public List<Object> getPositionalParams() {
        return positionalParams;
    }

    public void setPositionalParams(List<Object> positionalParams) {
        this.positionalParams = positionalParams;
        this.paramType = ParamType.POSITIONAL;
    }

    public ParamType getParamType() {
        return paramType;
    }

    @JsonIgnore
    public boolean isNamedParams() {
        return paramType == ParamType.NAMED;
    }

    @JsonIgnore
    public boolean isPositionalParams() {
        return paramType == ParamType.POSITIONAL;
    }

    @JsonIgnore
    public void setResult(FlameResult result) {
        this.result = result;
    }

    @JsonIgnore
    public FlameResult getResult() {
        return this.result;
    }

    public String getFullMethod() {
        if (thing != null && method != null) {
            return thing + ":" + method;
        }
        return method;
    }

    @JsonIgnore
    public String toString() {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("id:").append(this.getId()).append(",jsonrpc:").append(FlameMessage.JSONRPC_VERSION);
        strbuf.append(",thing:").append(this.getThing()).append(",method:").append(this.getMethod());
        if (paramType == ParamType.POSITIONAL && positionalParams != null) {
            strbuf.append(",positionalParams:").append(positionalParams.toString());
        } else if (params != null) {
            strbuf.append(",params:").append(params.toString());
        }
        return strbuf.toString();
    }
}
