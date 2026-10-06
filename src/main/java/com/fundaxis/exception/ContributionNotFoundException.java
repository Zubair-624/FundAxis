package com.fundaxis.exception;


/// This exception should be used whenever a specific contribution ID does not exist


public class ContributionNotFoundException extends RuntimeException {

    // Create a contribution not found error with a custom message
    public ContributionNotFoundException(String message) {
        super(message);

    }

}
