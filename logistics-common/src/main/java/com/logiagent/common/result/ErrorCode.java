package com.logiagent.common.result;

public enum ErrorCode {

    SUCCESS(200, "success"),
    PARAM_ERROR(400, "param error"),
    UNAUTHORIZED(401, "unauthorized"),
    FORBIDDEN(403, "forbidden"),
    NOT_FOUND(404, "not found"),
    SYSTEM_ERROR(500, "system error");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
