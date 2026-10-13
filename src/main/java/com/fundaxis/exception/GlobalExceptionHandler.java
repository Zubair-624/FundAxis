package com.fundaxis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


///----- Not Found -> 404 NOT FOUND -----///
///----- Duplicate -> 409 CONFLICT -----///



///==================== Global Exception Handler ====================///
// Handles exceptions for all REST controllers
@RestControllerAdvice
public class GlobalExceptionHandler {


    ///==================== Employee Exceptions ====================///

    ///---------- Handle Employee Not Found ----------
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///---------- Handle Duplicate Employee Data ----------
    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<String> handleDuplicateEmployeeException(DuplicateEmployeeException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///==================== Contribution Exceptions ====================///

    ///---------- Handle Contribution Not Found ----------
    @ExceptionHandler(ContributionNotFoundException.class)
    public ResponseEntity<String> handleContributionNotFoundException(ContributionNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///---------- Handle Duplicate Contribution Data ----------
    @ExceptionHandler(DuplicateContributionException.class)
    public ResponseEntity<String> handleDuplicateContributionException(DuplicateContributionException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///==================== Loan Exceptions ====================///

    ///---------- Handle Loan Not Found ----------
    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<String> handleLoanNotFoundException(LoanNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///---------- Handle Duplicate Loan Data ----------
    @ExceptionHandler(DuplicateLoanException.class)
    public ResponseEntity<String> handleDuplicateLoanException(DuplicateLoanException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///==================== Loan Extension Exceptions ====================///

    ///---------- Handle Loan Extension Not Found ----------
    @ExceptionHandler(LoanExtensionNotFoundException.class)
    public ResponseEntity<String> handleLoanExtensionNotFoundException(LoanExtensionNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///---------- Handle Duplicate Loan Extension ----------
    @ExceptionHandler(DuplicateLoanExtensionException.class)
    public ResponseEntity<String> handleDuplicateLoanExtensionException(DuplicateLoanExtensionException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///==================== Loan Disbursement Exceptions ====================///

    ///---------- Handle Loan Disbursement Not Found ----------
    @ExceptionHandler(LoanDisbursementNotFoundException.class)
    public ResponseEntity<String> handleLoanDisbursementNotFoundException(LoanDisbursementNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    ///==================== General Exceptions ====================///

    ///---------- Handle Invalid Lifecycle Or State Transitions ----------
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///---------- Handle Invalid Business Arguments ----------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());

    }


    //==================== LoanRepayment Schedule Exceptions ====================//

    // Handle LoanRepayment Schedule Not Found
    @ExceptionHandler(RepaymentScheduleNotFoundException.class)
    public ResponseEntity<String> handleRepaymentScheduleNotFoundException(RepaymentScheduleNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    // Handle Duplicate LoanRepayment Schedule
    @ExceptionHandler(DuplicateRepaymentScheduleException.class)
    public ResponseEntity<String> handleDuplicateRepaymentScheduleException(DuplicateRepaymentScheduleException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    //==================== Loan Repayment Exceptions ====================//

    // Handle Loan Repayment Not Found
    @ExceptionHandler(LoanRepaymentNotFoundException.class)
    public ResponseEntity<String> handleLoanRepaymentNotFoundException(LoanRepaymentNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }


    // Handle Duplicate Loan Repayment
    @ExceptionHandler(DuplicateLoanRepaymentException.class)
    public ResponseEntity<String> handleDuplicateLoanRepaymentException(DuplicateLoanRepaymentException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }


    ///==================== User Account Exceptions ====================///

    // Handle User Account Not Found
    @ExceptionHandler(UserAccountNotFoundException.class)
    public ResponseEntity<String> handleUserAccountNotFoundException(UserAccountNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

    }

    // Handle Duplicate User Account
    @ExceptionHandler(DuplicateUserAccountException.class)
    public ResponseEntity<String> handleDuplicateUserAccountException(DuplicateUserAccountException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());

    }



    //==================== Role Exceptions ====================//

    // Handle Role Not Found
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> handleRoleNotFoundException(
            RoleNotFoundException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }


    // Handle Duplicate Role
    @ExceptionHandler(DuplicateRoleException.class)
    public ResponseEntity<String> handleDuplicateRoleException(
            DuplicateRoleException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }
}