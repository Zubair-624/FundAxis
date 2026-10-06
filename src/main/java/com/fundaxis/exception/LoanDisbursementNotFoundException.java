package com.fundaxis.exception;

public class LoanDisbursementNotFoundException extends RuntimeException {

    // Create a loan disbursement not found error with a custom message
    public LoanDisbursementNotFoundException(String message) {
        super(message);
    }
}