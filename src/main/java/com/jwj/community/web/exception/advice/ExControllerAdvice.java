package com.jwj.community.web.exception.advice;

import com.jwj.community.web.exception.dto.ErrorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static java.util.Locale.getDefault;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@RequiredArgsConstructor
public class ExControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler
    @ResponseBody // ExceptionHandler에서 json으로 넘길 때!
    public ResponseEntity<ErrorResult> invalidRequestHandler(MethodArgumentNotValidException e){
        String errorMessage = messageSource.getMessage("error.badRequest", null, getDefault());

        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(BAD_REQUEST)
                .errorMessage(errorMessage)
                .exception(e)
                .build();

        for(FieldError fieldError : e.getFieldErrors()){
            errorResult.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(errorResult, BAD_REQUEST);
    }
}
