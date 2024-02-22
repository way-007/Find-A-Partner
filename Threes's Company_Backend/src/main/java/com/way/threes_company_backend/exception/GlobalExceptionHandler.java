package com.way.threes_company_backend.exception;

import com.way.threes_company_backend.common.BaseResponse;
import com.way.threes_company_backend.common.ErrorCode;
import com.way.threes_company_backend.common.ResponseUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 自定义的异常类的异常
    @ExceptionHandler(BusinessResponseException.class)
    public BaseResponse businessExceptionHandler(BusinessResponseException e) {
        return ResponseUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    // 系统运行时的异常
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(BusinessResponseException e) {
        return ResponseUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }
}
