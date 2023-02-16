package com.jwj.community.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.web.exception.dto.ErrorResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.getDefault;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageSource messageSource;
    private final ObjectMapper mapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        // 인증은 되었지만 인가 실패 시
        String errorMessage = messageSource.getMessage("auth.forbidden", null, getDefault());

        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(FORBIDDEN)
                .errorMessage(errorMessage)
                .exception(accessDeniedException)
                .build();

        response.setStatus(FORBIDDEN.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        mapper.writeValue(response.getWriter(), errorResult);
    }
}
