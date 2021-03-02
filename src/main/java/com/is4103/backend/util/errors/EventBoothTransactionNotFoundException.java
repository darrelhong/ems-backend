

package com.is4103.backend.util.errors;

import org.springframework.http.HttpStatus;

import me.alidg.errors.annotation.ExceptionMapping;

@ExceptionMapping(statusCode = HttpStatus.BAD_REQUEST, errorCode = "eventBoothTransaction.not_found")
public final class EventBoothTransactionNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EventBoothTransactionNotFoundException() {
        super();
    }

    public EventBoothTransactionNotFoundException(String message) {
        super(message);
    }

    public EventBoothTransactionNotFoundException(Throwable cause) {
        super(cause);
    }

    public EventBoothTransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}