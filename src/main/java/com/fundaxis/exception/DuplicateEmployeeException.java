package com.fundaxis.exception;

///----- Duplicate data should always use -> DuplicateEmployeeException -----
/// DuplicateEmployeeException -> for cases where an employee ID or email already exists
/// API will return -> [Duplicate employee data → "409 CONFLICT"]


// Custom exception for duplicate employee ID or email
public class DuplicateEmployeeException extends RuntimeException{

    // Create a duplicate employee error with a custom message
    public DuplicateEmployeeException(String message){
        super(message);

    }
}
