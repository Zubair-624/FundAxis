package com.fundaxis.controller;

import com.fundaxis.entity.Loan;
import com.fundaxis.entity.LoanDisbursement;
import com.fundaxis.service.LoanDisbursementService;
import com.fundaxis.service.LoanService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;


// Handles Loan Disbursement Management pages for administrators.
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminLoanDisbursementController {


    // ==================== Constructor Dependency Injection ==================== //

    private final LoanDisbursementService loanDisbursementService;
    private final LoanService loanService;


    public AdminLoanDisbursementController(
            LoanDisbursementService loanDisbursementService,
            LoanService loanService
    ) {
        this.loanDisbursementService = loanDisbursementService;
        this.loanService = loanService;
    }


    // ============================================================
    // 1. LOAN DISBURSEMENT LIST / SEARCH / FILTER
    // ============================================================ //

    // Displays all Loan Disbursement records and supports search/filter.
    @GetMapping("/admin/disbursements")
    public String loanDisbursementManagementPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String disbursementStatus,
            @RequestParam(required = false) String paymentMethod,
            Model model
    ) {

        // Retrieve the complete dataset.
        List<LoanDisbursement> allDisbursements =
                loanDisbursementService.getAllDisbursements();


        // ==================== Summary Cards ==================== //

        model.addAttribute(
                "totalDisbursements",
                allDisbursements.size()
        );


        long pendingDisbursements = allDisbursements.stream()
                .filter(disbursement ->
                        disbursement.getDisbursementStatus()
                                == LoanDisbursement.DisbursementStatus.PENDING
                )
                .count();

        model.addAttribute(
                "pendingDisbursements",
                pendingDisbursements
        );


        long processingDisbursements = allDisbursements.stream()
                .filter(disbursement ->
                        disbursement.getDisbursementStatus()
                                == LoanDisbursement.DisbursementStatus.PROCESSING
                )
                .count();

        model.addAttribute(
                "processingDisbursements",
                processingDisbursements
        );


        long completedDisbursements = allDisbursements.stream()
                .filter(disbursement ->
                        disbursement.getDisbursementStatus()
                                == LoanDisbursement.DisbursementStatus.DISBURSED
                )
                .count();

        model.addAttribute(
                "completedDisbursements",
                completedDisbursements
        );


        long failedCancelledDisbursements = allDisbursements.stream()
                .filter(disbursement ->
                        disbursement.getDisbursementStatus()
                                == LoanDisbursement.DisbursementStatus.FAILED
                                ||
                                disbursement.getDisbursementStatus()
                                        == LoanDisbursement.DisbursementStatus.CANCELLED
                )
                .count();

        model.addAttribute(
                "failedCancelledDisbursements",
                failedCancelledDisbursements
        );


        // ==================== Search / Filter ==================== //

        List<LoanDisbursement> filteredDisbursements =
                allDisbursements.stream()

                        // Keyword Search.
                        .filter(disbursement -> {

                            if (keyword == null
                                    || keyword.trim().isEmpty()) {

                                return true;
                            }


                            String searchValue =
                                    keyword.trim().toLowerCase();


                            // Search by Loan Application ID.
                            boolean loanApplicationIdMatches =
                                    disbursement.getLoan() != null
                                            && disbursement.getLoan()
                                            .getLoanApplicationId() != null
                                            && disbursement.getLoan()
                                            .getLoanApplicationId()
                                            .toLowerCase()
                                            .contains(searchValue);


                            // Search by Employee ID.
                            boolean employeeIdMatches =
                                    disbursement.getLoan() != null
                                            && disbursement.getLoan()
                                            .getEmployee() != null
                                            && disbursement.getLoan()
                                            .getEmployee()
                                            .getEmployeeId() != null
                                            && disbursement.getLoan()
                                            .getEmployee()
                                            .getEmployeeId()
                                            .toLowerCase()
                                            .contains(searchValue);


                            // Search by Employee Name.
                            boolean employeeNameMatches =
                                    disbursement.getLoan() != null
                                            && disbursement.getLoan()
                                            .getEmployee() != null
                                            && disbursement.getLoan()
                                            .getEmployee()
                                            .getFullName() != null
                                            && disbursement.getLoan()
                                            .getEmployee()
                                            .getFullName()
                                            .toLowerCase()
                                            .contains(searchValue);


                            // Search by Transaction Reference Number.
                            boolean referenceNumberMatches =
                                    disbursement.getReferenceNumber() != null
                                            && disbursement.getReferenceNumber()
                                            .toLowerCase()
                                            .contains(searchValue);


                            return loanApplicationIdMatches
                                    || employeeIdMatches
                                    || employeeNameMatches
                                    || referenceNumberMatches;
                        })


                        // Filter by Disbursement Status.
                        .filter(disbursement ->
                                disbursementStatus == null
                                        || disbursementStatus.isBlank()
                                        || (
                                        disbursement.getDisbursementStatus() != null
                                                && disbursement
                                                .getDisbursementStatus()
                                                .name()
                                                .equalsIgnoreCase(
                                                        disbursementStatus
                                                )
                                )
                        )


                        // Filter by Payment Method.
                        .filter(disbursement ->
                                paymentMethod == null
                                        || paymentMethod.isBlank()
                                        || (
                                        disbursement.getPaymentMethod() != null
                                                && disbursement
                                                .getPaymentMethod()
                                                .name()
                                                .equalsIgnoreCase(
                                                        paymentMethod
                                                )
                                )
                        )


                        .toList();


        // Pass filtered records to Thymeleaf.
        model.addAttribute(
                "disbursements",
                filteredDisbursements
        );


        // Preserve filter values.
        model.addAttribute(
                "keyword",
                keyword
        );

        model.addAttribute(
                "selectedDisbursementStatus",
                disbursementStatus
        );

        model.addAttribute(
                "selectedPaymentMethod",
                paymentMethod
        );


        return "admin/loan-disbursements";
    }


    // ============================================================
    // 2. SHOW ADD DISBURSEMENT FORM
    // ============================================================ //

    // Displays the form for creating a new Loan Disbursement.
    @GetMapping("/admin/disbursements/new")
    public String showAddDisbursementForm(Model model) {

        LoanDisbursement disbursement =
                new LoanDisbursement();


        model.addAttribute(
                "disbursement",
                disbursement
        );


        // Business Loan Application ID entered by the Admin.
        model.addAttribute(
                "loanApplicationId",
                ""
        );


        return "admin/loan-disbursements-form";
    }


    // ============================================================
    // 3. CREATE DISBURSEMENT
    // ============================================================ //

    // Creates a new PENDING disbursement for an APPROVED loan.
    @PostMapping("/admin/disbursements")
    public String createDisbursement(
            @RequestParam String loanApplicationId,
            @Valid
            @ModelAttribute("disbursement")
            LoanDisbursement disbursement,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {


        // ==================== Validate Loan Application ID ==================== //

        if (loanApplicationId == null
                || loanApplicationId.trim().isEmpty()) {

            model.addAttribute(
                    "errorMessage",
                    "Loan Application ID is required."
            );

            model.addAttribute(
                    "loanApplicationId",
                    loanApplicationId
            );

            return "admin/loan-disbursements-form";
        }


        // ==================== Validate Disbursement Fields ==================== //

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "loanApplicationId",
                    loanApplicationId
            );

            return "admin/loan-disbursements-form";
        }


        try {

            // Find parent Loan using its business identifier.
            Loan existingLoan =
                    loanService.getLoanByApplicationId(
                            loanApplicationId.trim()
                    );


            // Service requires the internal Loan database ID.
            loanDisbursementService.createDisbursement(
                    existingLoan.getId(),
                    disbursement
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan disbursement created successfully."
            );


            return "redirect:/admin/disbursements";


        } catch (RuntimeException exception) {

            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            model.addAttribute(
                    "loanApplicationId",
                    loanApplicationId
            );


            return "admin/loan-disbursements-form";
        }
    }


    // ============================================================
    // 4. VIEW DISBURSEMENT DETAILS
    // ============================================================ //

    // Displays one Loan Disbursement record.
    @GetMapping("/admin/disbursements/{id}")
    public String viewDisbursementDetails(
            @PathVariable Long id,
            Model model
    ) {

        LoanDisbursement disbursement =
                getDisbursementOrThrow(id);


        model.addAttribute(
                "disbursement",
                disbursement
        );


        // Show approved / already disbursed / remaining amounts.
        if (disbursement.getLoan() != null) {

            Long loanId =
                    disbursement.getLoan().getId();


            BigDecimal totalDisbursed =
                    loanDisbursementService
                            .getTotalDisbursedAmount(loanId);


            BigDecimal remainingApprovedAmount =
                    loanDisbursementService
                            .getRemainingApprovedAmount(loanId);


            model.addAttribute(
                    "totalDisbursedAmount",
                    totalDisbursed
            );

            model.addAttribute(
                    "remainingApprovedAmount",
                    remainingApprovedAmount
            );
        }


        return "admin/loan-disbursements-details";
    }


    // ============================================================
    // 5. START PROCESSING
    // ============================================================ //

    // Moves a PENDING disbursement to PROCESSING.
    @PostMapping("/admin/disbursements/{id}/start-processing")
    public String startProcessing(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            loanDisbursementService.startProcessing(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan disbursement processing started successfully."
            );


        } catch (IllegalStateException | IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return "redirect:/admin/disbursements/" + id;
    }


    // ============================================================
    // 6. COMPLETE DISBURSEMENT
    // ============================================================ //

    // Completes a PROCESSING disbursement and marks it DISBURSED.
    @PostMapping("/admin/disbursements/{id}/complete")
    public String completeDisbursement(
            @PathVariable Long id,
            @RequestParam String referenceNumber,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {


        // Reference number is required.
        if (referenceNumber == null
                || referenceNumber.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Reference number is required."
            );

            return "redirect:/admin/disbursements/" + id;
        }


        try {

            // Record the currently authenticated Admin.
            String processedBy =
                    authentication.getName();


            loanDisbursementService.completeDisbursement(
                    id,
                    processedBy,
                    referenceNumber.trim()
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan disbursement completed successfully."
            );


        } catch (IllegalStateException | IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return "redirect:/admin/disbursements/" + id;
    }


    // ============================================================
    // 7. FAIL DISBURSEMENT
    // ============================================================ //

    // Marks a PROCESSING disbursement as FAILED.
    @PostMapping("/admin/disbursements/{id}/fail")
    public String failDisbursement(
            @PathVariable Long id,
            @RequestParam String failureReason,
            RedirectAttributes redirectAttributes
    ) {


        if (failureReason == null
                || failureReason.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failure reason is required."
            );

            return "redirect:/admin/disbursements/" + id;
        }


        try {

            loanDisbursementService.failDisbursement(
                    id,
                    failureReason.trim()
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan disbursement marked as failed."
            );


        } catch (IllegalStateException | IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return "redirect:/admin/disbursements/" + id;
    }


    // ============================================================
    // 8. CANCEL DISBURSEMENT
    // ============================================================ //

    // Cancels a PENDING disbursement.
    @PostMapping("/admin/disbursements/{id}/cancel")
    public String cancelDisbursement(
            @PathVariable Long id,
            @RequestParam String cancellationReason,
            RedirectAttributes redirectAttributes
    ) {


        if (cancellationReason == null
                || cancellationReason.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cancellation reason is required."
            );

            return "redirect:/admin/disbursements/" + id;
        }


        try {

            loanDisbursementService.cancelDisbursement(
                    id,
                    cancellationReason.trim()
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan disbursement cancelled successfully."
            );


        } catch (IllegalStateException | IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return "redirect:/admin/disbursements/" + id;
    }


    // ============================================================
    // PRIVATE HELPER
    // ============================================================ //

    // Retrieves a disbursement using its database ID.
    private LoanDisbursement getDisbursementOrThrow(Long id) {

        return loanDisbursementService.getDisbursementById(id);
    }
}