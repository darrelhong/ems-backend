package com.is4103.backend.util.errors.ticketing;

public class TicketTransactionNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public TicketTransactionNotFoundException() {
        super();
    }

    public TicketTransactionNotFoundException(String message) {
        super(message);
    }
}
