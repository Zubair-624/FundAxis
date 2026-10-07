package com.fundaxis.controller;

import com.fundaxis.entity.LoanRepayment;
import com.fundaxis.service.LoanRepaymentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loan-repayments")
public class LoanRepaymentController {

    private final LoanRepaymentService loanRepaymentService;

    ///---------- Constructor Injection ----------
    public LoanRepaymentController(LoanRepaymentService loanRepaymentService) {
        this.loanRepaymentService = loanRepaymentService;

    }


    ///==================== Create Loan Repayment ====================///

    ///---------- Create A Repayment Transaction For One Installment ----------
    @PostMapping("/schedule/{repaymentScheduleId}")
    public ResponseEntity<LoanRepayment> createLoanRepayment(@PathVariable Long repaymentScheduleId, @Valid @RequestBody LoanRepayment loanRepayment) {

        LoanRepayment createdLoanRepayment = loanRepaymentService.createLoanRepayment(repaymentScheduleId, loanRepayment);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdLoanRepayment);

    }


    ///==================== Read Loan Repayment ====================///

    ///---------- Get All Repayment Transactions ----------
    @GetMapping
    public ResponseEntity<List<LoanRepayment>> getAllLoanRepayments() {

        return ResponseEntity.ok(loanRepaymentService.getAllLoanRepayments());

    }


    ///---------- Get Repayment By Database ID (id) ----------
    @GetMapping("/{id}")
    public ResponseEntity<LoanRepayment> getLoanRepaymentById(@PathVariable Long id) {

        return ResponseEntity.ok(loanRepaymentService.getLoanRepaymentById(id));

    }


    ///---------- Get All Repayments For One Installment ----------
    @GetMapping("/schedule/{repaymentScheduleId}")
    public ResponseEntity<List<LoanRepayment>> getLoanRepaymentsByScheduleId(@PathVariable Long repaymentScheduleId) {

        return ResponseEntity.ok(loanRepaymentService.getLoanRepaymentsByScheduleId(repaymentScheduleId));

    }


    ///---------- Get Repayment By Unique Transaction Reference ----------
    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<LoanRepayment> getLoanRepaymentByReferenceNumber(@PathVariable String referenceNumber) {

        return ResponseEntity.ok(loanRepaymentService.getLoanRepaymentByReferenceNumber(referenceNumber));

    }


    ///---------- Get Repayments By Processing Status ----------
    @GetMapping("/status/{repaymentStatus}")
    public ResponseEntity<List<LoanRepayment>> getLoanRepaymentsByStatus(@PathVariable LoanRepayment.RepaymentStatus repaymentStatus) {

        return ResponseEntity.ok(loanRepaymentService.getLoanRepaymentsByStatus(repaymentStatus));

    }


    ///---------- Get Repayments By Payment Method ----------
    @GetMapping("/payment-method/{paymentMethod}")
    public ResponseEntity<List<LoanRepayment>> getLoanRepaymentsByPaymentMethod(@PathVariable LoanRepayment.PaymentMethod paymentMethod) {

        return ResponseEntity.ok(loanRepaymentService.getLoanRepaymentsByPaymentMethod(paymentMethod));

    }


    ///---------- Get Repayments Made On A Specific Date ----------
    @GetMapping("/date/{repaymentDate}")
    public ResponseEntity<List<LoanRepayment>> getLoanRepaymentsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate repaymentDate) {

        return ResponseEntity.ok(loanRepaymentService.getLoanRepaymentsByDate(repaymentDate));

    }


    ///---------- Get All Repayments Belonging To One Loan ----------
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<LoanRepayment>> getLoanRepaymentsByLoanId(@PathVariable Long loanId) {

        return ResponseEntity.ok(loanRepaymentService.getLoanRepaymentsByLoanId(loanId));

    }


    ///---------- Get All Repayments Belonging To One Employee ----------
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LoanRepayment>> getEmployeeLoanRepayments(@PathVariable String employeeId) {

        return ResponseEntity.ok(loanRepaymentService.getEmployeeLoanRepayments(employeeId));

    }


    ///---------- Get An Employee's Repayments By Status ----------
    @GetMapping("/employee/{employeeId}/status/{repaymentStatus}")
    public ResponseEntity<List<LoanRepayment>> getEmployeeLoanRepaymentsByStatus(@PathVariable String employeeId, @PathVariable LoanRepayment.RepaymentStatus repaymentStatus) {

        return ResponseEntity.ok(loanRepaymentService.getEmployeeLoanRepaymentsByStatus(employeeId, repaymentStatus));

    }


    ///---------- Get Repayments For One Installment By Status ----------
    @GetMapping("/schedule/{repaymentScheduleId}/status/{repaymentStatus}")
    public ResponseEntity<List<LoanRepayment>> getScheduleLoanRepaymentsByStatus(@PathVariable Long repaymentScheduleId, @PathVariable LoanRepayment.RepaymentStatus repaymentStatus) {

        return ResponseEntity.ok(loanRepaymentService.getScheduleLoanRepaymentsByStatus(repaymentScheduleId, repaymentStatus));

    }


    ///==================== Loan Repayment Workflow ====================///

    ///---------- Start Processing A Pending Repayment ----------
    @PatchMapping("/{id}/process")
    public ResponseEntity<LoanRepayment> startProcessing(@PathVariable Long id) {

        return ResponseEntity.ok(loanRepaymentService.startProcessing(id));

    }


    ///---------- Complete A Processing Repayment ----------
    @PatchMapping("/{id}/complete")
    public ResponseEntity<LoanRepayment> completeLoanRepayment(@PathVariable Long id, @RequestParam String processedBy) {

        return ResponseEntity.ok(loanRepaymentService.completeLoanRepayment(id, processedBy));

    }


    ///---------- Mark A Processing Repayment As Failed ----------
    @PatchMapping("/{id}/fail")
    public ResponseEntity<LoanRepayment> failLoanRepayment(@PathVariable Long id, @RequestParam String failureReason) {

        return ResponseEntity.ok(loanRepaymentService.failLoanRepayment(id, failureReason));

    }


    ///---------- Cancel A Pending Repayment ----------
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<LoanRepayment> cancelLoanRepayment(@PathVariable Long id, @RequestParam String cancellationReason) {

        return ResponseEntity.ok(loanRepaymentService.cancelLoanRepayment(id, cancellationReason));

    }
}