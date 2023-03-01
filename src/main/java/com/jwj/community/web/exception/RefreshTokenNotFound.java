package com.jwj.community.web.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class RefreshTokenNotFound extends CommunityException {

    public RefreshTokenNotFound(String message) {
        super(message);
    }

    public RefreshTokenNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatusCode() {
        return UNAUTHORIZED;
    }
}
