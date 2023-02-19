package com.jwj.community.web.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
public abstract class CommunityException extends RuntimeException {

    public abstract HttpStatus getStatusCode();

    public CommunityException(String message) {
        super(message);
    }

    public CommunityException(String message, Throwable cause) {
        super(message, cause);
    }
}
