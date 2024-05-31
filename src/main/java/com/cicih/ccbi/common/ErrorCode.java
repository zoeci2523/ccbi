package com.cicih.ccbi.common;

/**
 * Application self-defined error code
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "wrong params"),
    NOT_LOGIN_ERROR(40100, "not login"),
    NO_AUTH_ERROR(40101, "no authentication"),
    NOT_FOUND_ERROR(40400, "data not found"),
    CREATE_ERROR(40401, "create error"),
    UPDATE_ERROR(40402, "update error"),
    DELETE_ERROR(40403, "delete error"),
    MSG_MISSING_ERROR(40404, "message missing error"),
    FORBIDDEN_ERROR(40300, "access denied"),
    SYSTEM_ERROR(50000, "system error"),
    OPERATION_ERROR(50001, "operation error");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
