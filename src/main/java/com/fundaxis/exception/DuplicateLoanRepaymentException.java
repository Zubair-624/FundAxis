package com.fundaxis.exception;

public class DuplicateLoanRepaymentException extends RuntimeException {
    public DuplicateLoanRepaymentException(String message) {
        super(message);
    }
}
