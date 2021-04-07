package com.is4103.backend.util.errors;

public class SellerApplicationNotFoundException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public SellerApplicationNotFoundException() {
        super();
    }

    public SellerApplicationNotFoundException(String message) {
        super(message);
    }
}
