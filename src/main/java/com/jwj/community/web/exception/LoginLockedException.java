package com.jwj.community.web.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginLockedException extends AuthenticationException {

    public LoginLockedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LoginLockedException(String msg) {
        super(msg);
    }
}
