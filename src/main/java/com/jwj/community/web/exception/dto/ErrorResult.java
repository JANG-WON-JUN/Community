package com.jwj.community.web.exception.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Data
public class ErrorResult {

    private final String errorCode;
    private final String errorMessage;
    private final Exception exception;
    private final String exceptionName;

    private final Map<String, String> validation = new HashMap<>();

    @Builder
    public ErrorResult(HttpStatus errorCode, String errorMessage, Exception exception){
        this.errorCode = String.valueOf(errorCode.value());
        this.errorMessage = errorMessage;
        this.exception = exception;
        this.exceptionName = exception.getClass().getSimpleName();
    }

    public void addValidation(String fieldName, String errorMessage) {
        validation.put(fieldName, errorMessage);
    }
}
