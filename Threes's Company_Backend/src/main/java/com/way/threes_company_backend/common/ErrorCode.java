package com.way.threes_company_backend.common;

/**
 * 前面的ResponseUtils返回的是成功的响应
 * 还有失败的响应对象需要进行返回：  步骤： 1. 首先需要定义一套错误响应码   因为每个错误是不一样的所以需要一套错误码来进行响应
 */
public enum ErrorCode {
    NO_OK_PARAMS("参数错误", "", 40000),
    NO_AUTHOR("没有权限", "", 40100),
    NO_LOGIN("未登录", "", 401001),
    SUCESS("ok", "", 0),
    SYSTEM_ERROR("系统异常", "", 50000),
    NO_REQUEST_PARAMS("请求参数为空", "", 40001);

    private final String message;

    private final String description;

    private  final int code;


    ErrorCode(String message, String description, int code) {
        this.message = message;
        this.description = description;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}
