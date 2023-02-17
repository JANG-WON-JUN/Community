package com.jwj.community.web.exception.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class ErrorResult {

    private final String errorCode;
    private final String errorMessage;
    private final String exceptionName;

    private final List<FieldError> fieldErrors = new ArrayList<>();

    @Builder
    public ErrorResult(HttpStatus errorCode, String errorMessage, Exception exception){
        this.errorCode = String.valueOf(errorCode.value());
        this.errorMessage = errorMessage;
        this.exceptionName = exception.getClass().getSimpleName();
    }

    public void addValidation(String fieldName, String errorMessage) {
        FieldError fieldError = FieldError.builder()
                .field(fieldName)
                .errorMessage(errorMessage)
                .build();

        fieldErrors.add(fieldError);
    }
}
