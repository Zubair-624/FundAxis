package com.fundaxis.exception;

public class DuplicateRepaymentScheduleException extends RuntimeException {
    public DuplicateRepaymentScheduleException(String message) {
        super(message);
    }
}
