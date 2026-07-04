package com.flame.rpc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FlameResult extends FlameRPC {
    public static final int PARSE_ERROR = -32700;
    public static final int INVALID_REQUEST = -32600;
    public static final int METHOD_NOT_FOUND = -32601;
    public static final int INVALID_PARAMS = -32602;
    public static final int INTERNAL_ERROR = -32603;

    public static final int SERVER_ERROR_MIN = -32000;
    public static final int SERVER_ERROR_MAX = -32099;
    public static final int TIMEOUT_ERROR = -32099;
    public static final int INTERNAL_EXCEPTION = -32098;

    @JsonProperty("error")
    protected Error error;
    @JsonProperty("result")
    protected Object result;

    public static class Error {
        @JsonProperty("code")
        protected Integer code;
        @JsonProperty("message")
        protected String message;
        @JsonProperty("data")
        protected Object data;

        public Error() {
        }

        public Error(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public Error(Integer code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public Integer getCode() {
            return this.code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @JsonInclude(Include.NON_NULL)
        public Object getData() {
            return this.data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Error{code=" + code + ", message='" + message + "'}";
        }
    }

    public FlameResult() {
        super();
    }

    public FlameResult(String result) {
        super();
        this.setResult(result);
    }

    public FlameResult(String id, Object result) {
        super(id);
        this.result = result;
    }

    @JsonInclude(Include.NON_NULL)
    public Error getError() {
        return error;
    }

    @JsonInclude(Include.NON_NULL)
    public void setError(Error error) {
        this.error = error;
    }

    public void setException(Throwable e) {
        setException(e, null);
    }

    public void setException(Throwable e, Object data) {
        if (this.error == null) {
            this.error = new Error();
        }
        if (e.getMessage() != null && e.getMessage().contains("time out")) {
            this.error.code = TIMEOUT_ERROR;
        } else if (e instanceof java.util.NoSuchElementException) {
            this.error.code = METHOD_NOT_FOUND;
        } else if (e instanceof IllegalArgumentException) {
            this.error.code = INVALID_PARAMS;
        } else {
            this.error.code = INTERNAL_EXCEPTION;
        }
        this.error.message = e.getMessage();
        this.error.data = data;
    }

    public void setError(int code, String message) {
        this.error = new Error(code, message);
    }

    public void setError(int code, String message, Object data) {
        this.error = new Error(code, message, data);
    }

    @JsonInclude(Include.NON_NULL)
    public Object getResult() {
        return result;
    }

    @JsonInclude(Include.NON_NULL)
    public void setResult(Object result) {
        this.result = result;
    }

    @JsonIgnore
    public boolean isError() {
        return error != null;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return error == null;
    }

    public static FlameResult ok() {
        FlameResult result = new FlameResult();
        result.setResult("");
        return result;
    }

    public static FlameResult ok(Object result) {
        FlameResult r = new FlameResult();
        r.setResult(result);
        return r;
    }

    public static FlameResult error(int code, String message) {
        FlameResult r = new FlameResult();
        r.setError(code, message);
        return r;
    }

    public static FlameResult error(int code, String message, Object data) {
        FlameResult r = new FlameResult();
        r.setError(code, message, data);
        return r;
    }

    public static FlameResult parseError(String message) {
        return error(PARSE_ERROR, message);
    }

    public static FlameResult invalidRequest(String message) {
        return error(INVALID_REQUEST, message);
    }

    public static FlameResult methodNotFound(String message) {
        return error(METHOD_NOT_FOUND, message != null ? message : "Method not found");
    }

    public static FlameResult invalidParams(String message) {
        return error(INVALID_PARAMS, message);
    }

    public static FlameResult internalError(String message) {
        return error(INTERNAL_ERROR, message);
    }

    @JsonIgnore
    public String toString() {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("id:").append(this.getId()).append(",jsonrpc:").append(FlameRPC.JSONRPC_VERSION);
        if (this.error != null) {
            strbuf.append(",error.code:").append(this.error.getCode()).append(",error.message:").append(this.error.getMessage());
        } else {
            strbuf.append(",result:").append(this.getResult());
        }
        return strbuf.toString();
    }
}
