package com.is4103.backend.util.errors;
import org.springframework.http.HttpStatus;

import me.alidg.errors.annotation.ExceptionMapping;

@ExceptionMapping(statusCode = HttpStatus.BAD_REQUEST, errorCode = "event.image.not_found")
public class EventImageNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    public EventImageNotFoundException() {
        super();
    }

    public EventImageNotFoundException(final String message) {
        super(message);
    }

}