package com.jwj.community.web.exception.advice;

import com.jwj.community.web.exception.CommunityException;
import com.jwj.community.web.exception.dto.ErrorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static java.util.Locale.getDefault;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@RequiredArgsConstructor
public class ExControllerAdvice {

    private final MessageSource messageSource;

    @ResponseBody // ExceptionHandler에서 json으로 넘길 때!
    @ExceptionHandler
    @SuppressWarnings("ConstantConditions") // @Nullable 제약조건을 suppress할 때 ConstantConditions를 사용한다.
    public ResponseEntity<ErrorResult> invalidRequestHandler(BindException e){
        String errorMessage = e.hasGlobalErrors() ?
                e.getBindingResult().getGlobalError().getDefaultMessage()
                :messageSource.getMessage("error.badRequest", null, getDefault());

        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(BAD_REQUEST)
                .errorMessage(errorMessage)
                .exception(e)
                .build();

        // 각 validator의 rejectValue, reject에서 messageSource의 코드를 전달하지 않고
        // defaultMessage에 messageSource를 사용하여 에러 메세지를 넣어준다.
        // (아래의 fieldError.getDefaultMessage() 로직을 어노테이션을 사용한 validation check와 validator를 사용한 validation check를 공통으로 사용하기 위함)
        for(FieldError fieldError : e.getFieldErrors()){
            errorResult.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(errorResult, BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler
    public ResponseEntity<ErrorResult> commonExHandler(CommunityException e){
        String errorMessage = messageSource.getMessage("error.noBoard", null, getDefault());

        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(e.getStatusCode())
                .errorMessage(e.getMessage())
                .exception(e)
                .build();

        return new ResponseEntity<>(errorResult, BAD_REQUEST);
    }
}
