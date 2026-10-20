package com.fundaxis.exception;

public class DuplicateRepaymentException extends RuntimeException {
    public DuplicateRepaymentException(String message) {
        super(message);
    }
}
