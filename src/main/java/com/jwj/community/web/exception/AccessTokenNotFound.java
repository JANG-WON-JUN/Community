package com.jwj.community.web.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class AccessTokenNotFound extends CommunityException {

    public AccessTokenNotFound() {
    }

    public AccessTokenNotFound(String message) {
        super(message);
    }

    public AccessTokenNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatusCode() {
        return UNAUTHORIZED;
    }
}
