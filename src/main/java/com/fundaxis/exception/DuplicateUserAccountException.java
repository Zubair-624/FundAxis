package com.fundaxis.exception;

public class DuplicateUserAccountException extends RuntimeException {
    public DuplicateUserAccountException(String message) {
        super(message);
    }
}
