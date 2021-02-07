package com.is4103.backend.util.errors;

import org.springframework.http.HttpStatus;

import me.alidg.errors.annotation.ExceptionMapping;

@ExceptionMapping(statusCode = HttpStatus.UNAUTHORIZED, errorCode = "jwt.expired_token")
public class CustomExpiredJwtException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CustomExpiredJwtException() {
        super();
    }
}
