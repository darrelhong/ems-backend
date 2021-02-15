package com.is4103.backend.util.errors;

import org.springframework.http.HttpStatus;

import me.alidg.errors.annotation.ExceptionMapping;

@ExceptionMapping(statusCode = HttpStatus.BAD_REQUEST, errorCode = "event.not_found")
public final class EventNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EventNotFoundException() {
        super();
    }

    public EventNotFoundException(String message) {
        super(message);
    }

    public EventNotFoundException(Throwable cause) {
        super(cause);
    }

    public EventNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
