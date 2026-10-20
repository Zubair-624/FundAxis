package com.fundaxis.controller;


import com.fundaxis.entity.Loan;
import com.fundaxis.entity.LoanExtension;
import com.fundaxis.service.LoanExtensionService;
import com.fundaxis.service.LoanService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


// Handles Loan Extension Management pages for administrators.
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminLoanExtensionController {


    // ==================== Constructor Dependency Injection ==================== //

    private final LoanExtensionService loanExtensionService;
    private final LoanService loanService;


    public AdminLoanExtensionController(
            LoanExtensionService loanExtensionService,
            LoanService loanService
    ) {
        this.loanExtensionService = loanExtensionService;
        this.loanService = loanService;
    }


    // ============================================================
    // 1. LOAN EXTENSION LIST / SEARCH / FILTER
    // ============================================================ //

    // Displays all loan extension records and supports search/filter.
    @GetMapping("/admin/loan-extensions")
    public String loanExtensionManagementPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String extensionStatus,
            Model model
    ) {

        // Retrieve the complete extension dataset.
        List<LoanExtension> allExtensions =
                loanExtensionService.getAllExtensions();


        // ==================== Summary Cards ==================== //

        model.addAttribute(
                "totalExtensions",
                allExtensions.size()
        );


        long pendingExtensions = allExtensions.stream()
                .filter(extension ->
                        extension.getExtensionStatus()
                                == LoanExtension.ExtensionStatus.PENDING
                )
                .count();

        model.addAttribute(
                "pendingExtensions",
                pendingExtensions
        );


        long approvedExtensions = allExtensions.stream()
                .filter(extension ->
                        extension.getExtensionStatus()
                                == LoanExtension.ExtensionStatus.APPROVED
                )
                .count();

        model.addAttribute(
                "approvedExtensions",
                approvedExtensions
        );


        long rejectedCancelledExtensions = allExtensions.stream()
                .filter(extension ->
                        extension.getExtensionStatus()
                                == LoanExtension.ExtensionStatus.REJECTED
                                || extension.getExtensionStatus()
                                == LoanExtension.ExtensionStatus.CANCELLED
                )
                .count();

        model.addAttribute(
                "rejectedCancelledExtensions",
                rejectedCancelledExtensions
        );


        // ==================== Search / Filter ==================== //

        List<LoanExtension> filteredExtensions =
                allExtensions.stream()

                        // Keyword search.
                        .filter(extension -> {

                            if (keyword == null
                                    || keyword.trim().isEmpty()) {

                                return true;
                            }


                            String searchValue =
                                    keyword.trim().toLowerCase();


                            // Search by Loan Application ID.
                            boolean loanApplicationIdMatches =
                                    extension.getLoan() != null
                                            && extension.getLoan()
                                            .getLoanApplicationId() != null
                                            && extension.getLoan()
                                            .getLoanApplicationId()
                                            .toLowerCase()
                                            .contains(searchValue);


                            // Search by Employee ID.
                            boolean employeeIdMatches =
                                    extension.getLoan() != null
                                            && extension.getLoan()
                                            .getEmployee() != null
                                            && extension.getLoan()
                                            .getEmployee()
                                            .getEmployeeId() != null
                                            && extension.getLoan()
                                            .getEmployee()
                                            .getEmployeeId()
                                            .toLowerCase()
                                            .contains(searchValue);


                            // Search by Employee Name.
                            boolean employeeNameMatches =
                                    extension.getLoan() != null
                                            && extension.getLoan()
                                            .getEmployee() != null
                                            && extension.getLoan()
                                            .getEmployee()
                                            .getFullName() != null
                                            && extension.getLoan()
                                            .getEmployee()
                                            .getFullName()
                                            .toLowerCase()
                                            .contains(searchValue);


                            return loanApplicationIdMatches
                                    || employeeIdMatches
                                    || employeeNameMatches;
                        })


                        // Filter by Extension Status.
                        .filter(extension ->
                                extensionStatus == null
                                        || extensionStatus.isBlank()
                                        || (
                                        extension.getExtensionStatus() != null
                                                && extension
                                                .getExtensionStatus()
                                                .name()
                                                .equalsIgnoreCase(
                                                        extensionStatus
                                                )
                                )
                        )

                        .toList();


        // Pass filtered records to Thymeleaf.
        model.addAttribute(
                "extensions",
                filteredExtensions
        );


        // Preserve filter values.
        model.addAttribute(
                "keyword",
                keyword
        );

        model.addAttribute(
                "selectedExtensionStatus",
                extensionStatus
        );


        return "admin/loan-extensions";
    }


    // ============================================================
    // 2. SHOW ADD EXTENSION REQUEST FORM
    // ============================================================ //

    // Displays the form for creating a new Loan Extension request.
    @GetMapping("/admin/loan-extensions/new")
    public String showAddExtensionForm(Model model) {

        // Create an empty extension object for form binding.
        LoanExtension extension =
                new LoanExtension();


        model.addAttribute(
                "extension",
                extension
        );


        // Empty Loan Application ID for create mode.
        model.addAttribute(
                "loanApplicationId",
                ""
        );


        // Tell the shared form that this is create mode.
        model.addAttribute(
                "editMode",
                false
        );


        return "admin/loan-extension-form";
    }


    // ============================================================
    // 3. CREATE EXTENSION REQUEST
    // ============================================================ //

    // Creates a Loan Extension request for an existing loan.
    @PostMapping("/admin/loan-extensions")
    public String createExtensionRequest(
            @RequestParam String loanApplicationId,
            @Valid
            @ModelAttribute("extension")
            LoanExtension extension,
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

            model.addAttribute(
                    "editMode",
                    false
            );

            return "admin/loan-extension-form";
        }


        // ==================== Validate Extension Fields ==================== //

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "loanApplicationId",
                    loanApplicationId
            );

            model.addAttribute(
                    "editMode",
                    false
            );

            return "admin/loan-extension-form";
        }


        try {

            // Find the parent loan using its business identifier.
            Loan existingLoan =
                    loanService.getLoanByApplicationId(
                            loanApplicationId.trim()
                    );


            // Existing service uses the internal Loan database ID.
            loanExtensionService.createExtensionRequest(
                    existingLoan.getId(),
                    extension
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan extension request created successfully."
            );


            return "redirect:/admin/loan-extensions";


        } catch (RuntimeException exception) {

            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            model.addAttribute(
                    "loanApplicationId",
                    loanApplicationId
            );

            model.addAttribute(
                    "editMode",
                    false
            );


            return "admin/loan-extension-form";
        }
    }


    // ============================================================
    // 4. VIEW EXTENSION DETAILS
    // ============================================================ //

    // Displays one Loan Extension record.
    @GetMapping("/admin/loan-extensions/{id}")
    public String viewExtensionDetails(
            @PathVariable Long id,
            Model model
    ) {

        LoanExtension extension =
                getExtensionOrThrow(id);


        model.addAttribute(
                "extension",
                extension
        );


        return "admin/loan-extension-details";
    }


    // ============================================================
    // 5. SHOW EDIT PENDING EXTENSION FORM
    // ============================================================ //

    // Displays the edit form only for a PENDING extension.
    @GetMapping("/admin/loan-extensions/{id}/edit")
    public String showEditExtensionForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        LoanExtension extension =
                getExtensionOrThrow(id);


        // Only PENDING extensions should be edited.
        if (extension.getExtensionStatus()
                != LoanExtension.ExtensionStatus.PENDING) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Only pending extension requests can be edited."
            );


            return "redirect:/admin/loan-extensions/" + id;
        }


        model.addAttribute(
                "extension",
                extension
        );


        // Display the business Loan Application ID.
        model.addAttribute(
                "loanApplicationId",
                extension.getLoan()
                        .getLoanApplicationId()
        );


        model.addAttribute(
                "editMode",
                true
        );


        return "admin/loan-extension-form";
    }


    // ============================================================
    // 6. UPDATE PENDING EXTENSION
    // ============================================================ //

    // Updates an existing PENDING Loan Extension request.
    @PostMapping("/admin/loan-extensions/{id}/update")
    public String updateExtension(
            @PathVariable Long id,
            @Valid
            @ModelAttribute("extension")
            LoanExtension extension,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {


        // Retrieve the existing extension.
        LoanExtension existingExtension =
                getExtensionOrThrow(id);

        extension.setId(id); // Restore the id so form links still work after an error


        // ==================== Validation Errors ==================== //

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "loanApplicationId",
                    existingExtension
                            .getLoan()
                            .getLoanApplicationId()
            );

            model.addAttribute(
                    "editMode",
                    true
            );


            return "admin/loan-extension-form";
        }


        try {

            // Existing service handles the business rules.
            loanExtensionService.updateExtension(
                    id,
                    extension
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan extension request updated successfully."
            );


            return "redirect:/admin/loan-extensions/" + id;


        } catch (IllegalStateException | IllegalArgumentException exception) { // Also show date-check errors in the form

            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            model.addAttribute(
                    "loanApplicationId",
                    existingExtension
                            .getLoan()
                            .getLoanApplicationId()
            );


            model.addAttribute(
                    "editMode",
                    true
            );


            return "admin/loan-extension-form";
        }
    }


    // ============================================================
    // 7. APPROVE EXTENSION
    // ============================================================ //

    // Approves a pending Loan Extension request.
    @PostMapping("/admin/loan-extensions/{id}/approve")
    public String approveExtension(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {

        try {

            // Use the authenticated Admin username as approvedBy.
            String approvedBy =
                    authentication.getName();


            loanExtensionService.approveExtension(
                    id,
                    approvedBy
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan extension approved successfully."
            );


            return "redirect:/admin/loan-extensions/" + id;


        } catch (IllegalStateException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            return "redirect:/admin/loan-extensions/" + id;
        }
    }


    // ============================================================
    // 8. REJECT EXTENSION
    // ============================================================ //

    // Rejects a pending Loan Extension request.
    @PostMapping("/admin/loan-extensions/{id}/reject")
    public String rejectExtension(
            @PathVariable Long id,
            @RequestParam String rejectionReason,
            RedirectAttributes redirectAttributes
    ) {


        // Rejection reason is required.
        if (rejectionReason == null
                || rejectionReason.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Rejection reason is required."
            );


            return "redirect:/admin/loan-extensions/" + id;
        }


        try {

            loanExtensionService.rejectExtension(
                    id,
                    rejectionReason.trim()
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan extension rejected successfully."
            );


            return "redirect:/admin/loan-extensions/" + id;


        } catch (IllegalStateException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            return "redirect:/admin/loan-extensions/" + id;
        }
    }


    // ============================================================
    // 9. CANCEL EXTENSION
    // ============================================================ //

    // Cancels a pending Loan Extension request.
    @PostMapping("/admin/loan-extensions/{id}/cancel")
    public String cancelExtension(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            loanExtensionService.cancelExtension(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Loan extension cancelled successfully."
            );


            return "redirect:/admin/loan-extensions/" + id;


        } catch (IllegalStateException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            return "redirect:/admin/loan-extensions/" + id;
        }
    }


    // ============================================================
    // PRIVATE HELPER
    // ============================================================ //

    // Retrieves an extension using its database ID.
    private LoanExtension getExtensionOrThrow(Long id) {

        return loanExtensionService.getExtensionById(id);
    }
}