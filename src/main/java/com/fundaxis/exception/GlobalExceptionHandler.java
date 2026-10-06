package com.fundaxis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// Handles exceptions for all REST controllers in one place
@RestControllerAdvice
public class GlobalExceptionHandler {


    ///========== Handle employee not found errors ==========///
    // Example: update, activate, or deactivate a non-existing employee
    // Returns HTTP 404 NOT FOUND
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///========== Handle duplicate employee data ==========///
    // Example: duplicate employee ID or duplicate email
    // Returns HTTP 409 CONFLICT
    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<String> handleDuplicateEmployeeException(DuplicateEmployeeException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///========== Handle requests that conflict with the employee's current status ==========///
    // Example: activating an already active employee or deactivating an already inactive employee
    // Returns HTTP 409 CONFLICT
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///========== Handle invalid argument errors ==========///
    // Example: marking a contribution as paid without a payment date
    // Returns HTTP 400 BAD REQUEST
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());

    }


    ///========== Handle contribution not found errors ==========///
    // Example: updating or retrieving a non-existing contribution
    // Returns HTTP 404 NOT FOUND
    @ExceptionHandler(ContributionNotFoundException.class)
    public ResponseEntity<String> handleContributionNotFoundException(ContributionNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///========== Handle duplicate contribution data ==========///
    // Example: adding a contribution for an employee who already has one for the same month
    // Returns HTTP 409 CONFLICT
    @ExceptionHandler(DuplicateContributionException.class)
    public ResponseEntity<String> handleDuplicateContributionException(DuplicateContributionException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


}