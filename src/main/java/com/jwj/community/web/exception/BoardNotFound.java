package com.jwj.community.web.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BoardNotFound extends CommunityException{

    public BoardNotFound(String message) {
        super(message);
    }

    public BoardNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatusCode() {
        return BAD_REQUEST;
    }
}
