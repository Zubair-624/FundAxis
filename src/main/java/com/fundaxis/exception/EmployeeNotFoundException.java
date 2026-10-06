package com.fundaxis.exception;

/// EmployeeNotFoundException -> for cases where an employee does not exist
/// API will return -> [Employee not found → "404 NOT FOUND"]


// Custom exception for cases where an employee cannot be found
public class EmployeeNotFoundException extends RuntimeException{

    // Create a duplicate employee error with a custom message
    public EmployeeNotFoundException(String message){
        super(message);
    }


}
