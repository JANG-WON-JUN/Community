package com.jwj.community.web.exception.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResult {

    private final String errorCode;
    private final String errorMessage;
    private final Exception exception;
    private final String exceptionName;

    @Builder
    public ErrorResult(HttpStatus errorCode, String errorMessage, Exception exception){
        this.errorCode = String.valueOf(errorCode.value());
        this.errorMessage = errorMessage;
        this.exception = exception;
        this.exceptionName = exception.getClass().getSimpleName();
    }
}
