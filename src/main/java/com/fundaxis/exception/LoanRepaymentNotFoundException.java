package com.fundaxis.exception;

public class LoanRepaymentNotFoundException extends RuntimeException {
    public LoanRepaymentNotFoundException(String message) {
        super(message);
    }
}
