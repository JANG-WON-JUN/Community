package com.jwj.community.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.web.exception.dto.ErrorResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.getDefault;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final MessageSource messageSource;
    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response
            , AuthenticationException exception) throws IOException {
        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(UNAUTHORIZED)
                .errorMessage(getErrorMessage(exception))
                .exception(exception)
                .build();

        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        mapper.writeValue(response.getWriter(), errorResult);
    }

    private String getErrorMessage(AuthenticationException exception) {
        // 공통 메세지를 사용하려면 예외 던질 시 메세지에 null을 넣는다.
        if(exception.getMessage() != null) {
            return exception.getMessage();
        }

        if (exception instanceof UsernameNotFoundException) {
            return messageSource.getMessage("confirm.email", null, getDefault());
        }

        if (exception instanceof BadCredentialsException) {
            return messageSource.getMessage("confirm.pwd", null, getDefault());
        }

        if (exception instanceof InsufficientAuthenticationException) {
            return messageSource.getMessage("error.extraAuth", null, getDefault());
        }

        return messageSource.getMessage("confirm.emailOrPassword", null, getDefault());
    }
}
