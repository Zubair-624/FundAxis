package com.fundaxis.exception;

public class RepaymentNotFoundException extends RuntimeException {
    public RepaymentNotFoundException(String message) {
        super(message);
    }
}
