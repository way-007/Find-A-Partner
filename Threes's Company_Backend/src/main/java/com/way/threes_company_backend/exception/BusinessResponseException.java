package com.way.threes_company_backend.exception;

import com.way.threes_company_backend.common.ErrorCode;

/**
 * 定义一个全局的业务异常处理类
 */
public class BusinessResponseException extends RuntimeException {

    private final int code;
    private final String description;

    public BusinessResponseException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessResponseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessResponseException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }


    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
