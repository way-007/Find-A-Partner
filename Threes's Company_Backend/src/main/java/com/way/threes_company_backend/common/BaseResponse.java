package com.way.threes_company_backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装一个基本的响应对象   code  data   message
 */

@Data
public class BaseResponse<T> implements Serializable {
    private String message;

    private int code;

    private T data;

    private String description;

    // 创建两个构造方法  方便创建新的不同参数的实例化对象



    // 三个参数的构造方法
    public BaseResponse(String message, int code, T data, String description) {
        this.message = message;
        this.code = code;
        this.data = data;
        this.description = description;
    }

    // 两个参数的构造方法  没有message
    public BaseResponse(int code, T data) {
        this("", code, data, "");
    }

    // 两个参数的构造方法  没有message
    public BaseResponse(String message, int code, T data) {
        this(message, code, data, "");
    }

    // 两个参数的构造方法  没有message
    public BaseResponse( int code, T data, String description) {
        this("", code, data, description);
    }

    // 将错误响应状态码也封装到该构造函数里面
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getMessage(), errorCode.getCode(), null, errorCode.getDescription());
    }


    // 将错误响应状态码也封装到该构造函数里面
    public BaseResponse(ErrorCode errorCode, String description) {
        this(errorCode.getMessage(), errorCode.getCode(), null, description);
    }


}
