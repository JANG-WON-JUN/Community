package com.jwj.community.config.security.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.web.exception.dto.ErrorResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.getDefault;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;
    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        // 익명사용자가 인가가 필요한 자원에 접근하였을 때 처리
        String errorMessage = messageSource.getMessage("auth.unauthorized", null, getDefault());

        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(UNAUTHORIZED)
                .errorMessage(errorMessage)
                .exception(authException)
                .build();

        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        mapper.writeValue(response.getWriter(), errorResult);
    }
}