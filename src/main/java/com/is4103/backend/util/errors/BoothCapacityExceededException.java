package com.is4103.backend.util.errors;

import org.springframework.http.HttpStatus;

import me.alidg.errors.annotation.ExceptionMapping;

@ExceptionMapping(statusCode = HttpStatus.BAD_REQUEST, errorCode = "booth.capacity_exceeded")
public class BoothCapacityExceededException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BoothCapacityExceededException() {
        super();
    }
}
