package com.fundaxis.controller;

import com.fundaxis.entity.LoanExtension;
import com.fundaxis.service.LoanExtensionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan-extensions")
public class LoanExtensionController {

    private final LoanExtensionService loanExtensionService;

    public LoanExtensionController(LoanExtensionService loanExtensionService) {
        this.loanExtensionService = loanExtensionService;

    }


    ///==================== Create Loan Extension ====================///

    ///---------- Create A New Extension Request For A Loan ----------
    @PostMapping("/loan/{loanId}")
    public ResponseEntity<LoanExtension> createExtensionRequest(@PathVariable Long loanId, @Valid @RequestBody LoanExtension loanExtension) {

        LoanExtension createdExtension = loanExtensionService.createExtensionRequest(loanId, loanExtension);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdExtension);

    }


    ///==================== Read Loan Extension ====================///

    ///---------- Get All Extension Requests ----------
    @GetMapping
    public ResponseEntity<List<LoanExtension>> getAllExtensions() {

        return ResponseEntity.ok(loanExtensionService.getAllExtensions());

    }


    ///---------- Get Extension Request By Database ID (id) ----------
    @GetMapping("/id/{id}")
    public ResponseEntity<LoanExtension> getExtensionById(@PathVariable Long id) {

        LoanExtension extension = loanExtensionService.getExtensionById(id);

        return ResponseEntity.ok(extension);

    }


    ///---------- Get All Extension Requests For A Specific Loan ----------
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<LoanExtension>> getExtensionsByLoanId(@PathVariable Long loanId) {

        return ResponseEntity.ok(loanExtensionService.getExtensionsByLoanId(loanId));

    }


    ///---------- Get Extension Requests By Status ----------
    @GetMapping("/status/{extensionStatus}")
    public ResponseEntity<List<LoanExtension>> getExtensionsByStatus(@PathVariable LoanExtension.ExtensionStatus extensionStatus) {

        return ResponseEntity.ok(loanExtensionService.getExtensionsByStatus(extensionStatus));

    }


    ///---------- Get All Extension Requests For An Employee's Loans ----------
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LoanExtension>> getEmployeeExtensions(@PathVariable String employeeId) {

        return ResponseEntity.ok(loanExtensionService.getEmployeeExtensions(employeeId));

    }


    ///---------- Get An Employee's Extension Requests By Status ----------
    @GetMapping("/employee/{employeeId}/status/{extensionStatus}")
    public ResponseEntity<List<LoanExtension>> getEmployeeExtensionsByStatus(@PathVariable String employeeId, @PathVariable LoanExtension.ExtensionStatus extensionStatus) {

        return ResponseEntity.ok(loanExtensionService.getEmployeeExtensionsByStatus(employeeId, extensionStatus));

    }


    ///==================== Update Loan Extension ====================///

    ///---------- Update A Pending Extension Request ----------
    @PutMapping("/{id}")
    public ResponseEntity<LoanExtension> updateExtension(@PathVariable Long id, @Valid @RequestBody LoanExtension loanExtension) {

        LoanExtension updatedExtension = loanExtensionService.updateExtension(id, loanExtension);

        return ResponseEntity.ok(updatedExtension);

    }


    ///==================== Loan Extension Workflow ====================///

    ///---------- Approve A Pending Extension Request ----------
    @PatchMapping("/{id}/approve")
    public ResponseEntity<LoanExtension> approveExtension(@PathVariable Long id, @RequestParam String approvedBy) {

        LoanExtension approvedExtension = loanExtensionService.approveExtension(id, approvedBy);

        return ResponseEntity.ok(approvedExtension);

    }


    ///---------- Reject A Pending Extension Request ----------
    @PatchMapping("/{id}/reject")
    public ResponseEntity<LoanExtension> rejectExtension(@PathVariable Long id, @RequestParam String rejectionReason) {

        LoanExtension rejectedExtension = loanExtensionService.rejectExtension(id, rejectionReason);

        return ResponseEntity.ok(rejectedExtension);

    }


    ///---------- Cancel A Pending Extension Request ----------
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<LoanExtension> cancelExtension(@PathVariable Long id) {

        LoanExtension cancelledExtension = loanExtensionService.cancelExtension(id);

        return ResponseEntity.ok(cancelledExtension);

    }
}