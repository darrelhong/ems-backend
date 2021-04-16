package com.is4103.backend.util.errors;
import org.springframework.http.HttpStatus;

import me.alidg.errors.annotation.ExceptionMapping;

@ExceptionMapping(statusCode = HttpStatus.BAD_REQUEST, errorCode = "profile.brochure.not_found")
public class BrochureNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    public BrochureNotFoundException() {
        super();
    }

    public BrochureNotFoundException(final String message) {
        super(message);
    }

}