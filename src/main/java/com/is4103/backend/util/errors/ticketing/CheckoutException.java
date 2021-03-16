package com.is4103.backend.util.errors.ticketing;

import org.springframework.http.HttpStatus;

import me.alidg.errors.annotation.ExceptionMapping;

@ExceptionMapping(statusCode = HttpStatus.INTERNAL_SERVER_ERROR, errorCode = "ticketing.checkout_error")
public class CheckoutException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CheckoutException() {
        super();
    }

    public CheckoutException(String message) {
        super(message);
    }
}
