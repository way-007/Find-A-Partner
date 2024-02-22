package com.way.threes_company_backend.common;


/**
 * 封装一个实例化返回对象的工具函数
 *   1. 返回成功的响应
 *   2. 返回失败的响应
 */
public class ResponseUtils {

    // 成功的返回
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>("", 0, data, "");
    }


    // 失败的返回  传入参数为统一的错误码对象
    public static BaseResponse error(ErrorCode errorCode) {
//        return new BaseResponse<>(errorCode.getMessage(), errorCode.getCode(), null, errorCode.getDescription());
        return new BaseResponse<>(errorCode);   // 因为我们在baseresponse里面已经写了一个相同的构造函数  我们只需要直接将errorcode传递进去即可
    }

    // 失败的返回  传入参数为统一的错误码对象
    public static BaseResponse error(int code,  String message, String description) {
//        return new BaseResponse<>(errorCode.getMessage(), errorCode.getCode(), null, errorCode.getDescription());
        return new BaseResponse<>(message, code, null, description);   // 因为我们在baseresponse里面已经写了一个相同的构造函数  我们只需要直接将errorcode传递进去即可
    }

    // 失败的返回  传入参数为统一的错误码对象
    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
//        return new BaseResponse<>(errorCode.getMessage(), errorCode.getCode(), null, errorCode.getDescription());
        return new BaseResponse<>(errorCode.getMessage(), errorCode.getCode(), message, description);   // 因为我们在baseresponse里面已经写了一个相同的构造函数  我们只需要直接将errorcode传递进去即可
    }


    // 失败的返回  传入参数为统一的错误码对象
    public static BaseResponse error(ErrorCode errorCode, String message) {
//        return new BaseResponse<>(errorCode.getMessage(), errorCode.getCode(), null, errorCode.getDescription());
        return new BaseResponse<>(errorCode.getCode(),null, message);   // 因为我们在baseresponse里面已经写了一个相同的构造函数  我们只需要直接将errorcode传递进去即可
    }



}
