package com.fundaxis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

///==================== Global Exception Handler ====================///
// Handles exceptions for all REST controllers in one place
@RestControllerAdvice
public class GlobalExceptionHandler {


    ///==================== Employee Exceptions ====================///

    ///---------- Handle Employee Not Found ----------
    // Example: update, activate, or deactivate a non-existing employee
    // Returns HTTP 404 NOT FOUND
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///---------- Handle Duplicate Employee Data ----------
    // Example: duplicate Employee ID or duplicate email
    // Returns HTTP 409 CONFLICT
    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<String> handleDuplicateEmployeeException(DuplicateEmployeeException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///==================== Contribution Exceptions ====================///

    ///---------- Handle Contribution Not Found ----------
    // Example: updating or retrieving a non-existing contribution
    // Returns HTTP 404 NOT FOUND
    @ExceptionHandler(ContributionNotFoundException.class)
    public ResponseEntity<String> handleContributionNotFoundException(ContributionNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///---------- Handle Duplicate Contribution Data ----------
    // Example: same employee already has a contribution for the same month
    // Returns HTTP 409 CONFLICT
    @ExceptionHandler(DuplicateContributionException.class)
    public ResponseEntity<String> handleDuplicateContributionException(DuplicateContributionException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///==================== Loan Exceptions ====================///

    ///---------- Handle Loan Not Found ----------
    // Example: retrieving or updating a non-existing loan
    // Returns HTTP 404 NOT FOUND
    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<String> handleLoanNotFoundException(LoanNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///---------- Handle Duplicate Loan Data ----------
    // Example: duplicate Loan Application ID
    // Returns HTTP 409 CONFLICT
    @ExceptionHandler(DuplicateLoanException.class)
    public ResponseEntity<String> handleDuplicateLoanException(DuplicateLoanException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///==================== General Exceptions ====================///

    ///---------- Handle Requests That Conflict With The Current Resource Status ----------
    // Example: updating a non-DRAFT loan or approving a non-eligible loan
    // Returns HTTP 409 CONFLICT
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///---------- Handle Invalid Business Arguments ----------
    // Example: invalid payment date, approved amount, or interest rate
    // Returns HTTP 400 BAD REQUEST
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());

    }
}