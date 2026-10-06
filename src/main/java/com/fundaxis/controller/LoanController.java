package com.fundaxis.controller;

import com.fundaxis.dto.LoanApprovalRequest;
import com.fundaxis.dto.LoanEligibilityRequest;
import com.fundaxis.dto.LoanRejectionRequest;
import com.fundaxis.entity.Loan;
import com.fundaxis.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;

    }


    ///==================== Create Loan ====================///

    ///---------- Create A New Loan For An Employee ----------
    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<Loan> createLoan(@PathVariable String employeeId, @Valid @RequestBody Loan loan) {

        Loan createdLoan = loanService.createLoan(employeeId, loan);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);

    }


    ///==================== Read Loan ====================///

    ///---------- Get All Loans ----------
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {

        return ResponseEntity.ok(loanService.getAllLoans());

    }


    ///---------- Get Loan By Database ID (id) ----------
    @GetMapping("/id/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {

        return ResponseEntity.ok(loanService.getLoanById(id));

    }


    ///---------- Get Loan By Application ID ----------
    @GetMapping("/application/{loanApplicationId}")
    public ResponseEntity<Loan> getLoanByApplicationId(@PathVariable String loanApplicationId) {

        return ResponseEntity.ok(loanService.getLoanByApplicationId(loanApplicationId));

    }


    ///---------- Get All Loans Of An Employee ----------
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Loan>> getEmployeeLoans(@PathVariable String employeeId) {

        return ResponseEntity.ok(loanService.getEmployeeLoans(employeeId));

    }


    ///---------- Get Loans By Loan Status ----------
    @GetMapping("/status/{loanStatus}")
    public ResponseEntity<List<Loan>> getLoansByLoanStatus(@PathVariable Loan.LoanStatus loanStatus) {

        return ResponseEntity.ok(loanService.getLoansByLoanStatus(loanStatus));

    }


    ///---------- Get Loans By Eligibility Status ----------
    @GetMapping("/eligibility/{eligibilityStatus}")
    public ResponseEntity<List<Loan>> getLoansByEligibilityStatus(@PathVariable Loan.EligibilityStatus eligibilityStatus) {

        return ResponseEntity.ok(loanService.getLoansByEligibilityStatus(eligibilityStatus));

    }


    ///---------- Get Loans By Loan Type ----------
    @GetMapping("/type/{loanType}")
    public ResponseEntity<List<Loan>> getLoansByType(@PathVariable Loan.LoanType loanType) {

        return ResponseEntity.ok(loanService.getLoansByType(loanType));

    }


    ///---------- Get Employee Loans By Status ----------
    @GetMapping("/employee/{employeeId}/status/{loanStatus}")
    public ResponseEntity<List<Loan>> getEmployeeLoansByStatus(@PathVariable String employeeId, @PathVariable Loan.LoanStatus loanStatus) {

        return ResponseEntity.ok(loanService.getEmployeeLoansByStatus(employeeId, loanStatus));

    }


    ///---------- Get Employee Loans By Eligibility Status ----------
    @GetMapping("/employee/{employeeId}/eligibility/{eligibilityStatus}")
    public ResponseEntity<List<Loan>> getEmployeeLoansByEligibilityStatus(@PathVariable String employeeId, @PathVariable Loan.EligibilityStatus eligibilityStatus) {

        return ResponseEntity.ok(loanService.getEmployeeLoansByEligibilityStatus(employeeId, eligibilityStatus));

    }


    ///==================== Update Loan ====================///

    ///---------- Update Editable Information Of A DRAFT Loan ----------
    @PutMapping("/{id}")
    public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @Valid @RequestBody Loan loan) {

        Loan updatedLoan = loanService.updateLoan(id, loan);

        return ResponseEntity.ok(updatedLoan);

    }


    ///==================== Loan Workflow ====================///

    ///---------- Submit A DRAFT Loan ----------
    @PatchMapping("/{id}/submit")
    public ResponseEntity<Loan> submitLoan(@PathVariable Long id) {

        return ResponseEntity.ok(loanService.submitLoan(id));

    }


    ///---------- Move A SUBMITTED Loan Into Review ----------
    @PatchMapping("/{id}/review")
    public ResponseEntity<Loan> startLoanReview(@PathVariable Long id) {

        return ResponseEntity.ok(loanService.startLoanReview(id));

    }


    ///---------- Update Eligibility While The Loan Is Under Review ----------
    @PatchMapping("/{id}/eligibility")
    public ResponseEntity<Loan> updateEligibilityStatus(@PathVariable Long id, @Valid @RequestBody LoanEligibilityRequest request) {

        return ResponseEntity.ok(loanService.updateEligibilityStatus(id, request.getEligibilityStatus()));

    }


    ///---------- Approve An Eligible Loan ----------
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Loan> approveLoan(@PathVariable Long id, @Valid @RequestBody LoanApprovalRequest request) {

        return ResponseEntity.ok(loanService.approveLoan(id, request.getApprovedAmount(), request.getInterestRate()));

    }


    ///---------- Reject A Loan That Is Under Review ----------
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Loan> rejectLoan(@PathVariable Long id, @Valid @RequestBody LoanRejectionRequest request) {

        return ResponseEntity.ok(loanService.rejectLoan(id, request.getRejectionReason()));

    }


    ///---------- Cancel A DRAFT Or SUBMITTED Loan ----------
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Loan> cancelLoan(@PathVariable Long id) {

        return ResponseEntity.ok(loanService.cancelLoan(id));

    }


}