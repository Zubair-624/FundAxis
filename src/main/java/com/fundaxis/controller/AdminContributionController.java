package com.fundaxis.controller;

import com.fundaxis.entity.Contribution;
import com.fundaxis.service.ContributionService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.fundaxis.exception.DuplicateContributionException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

// Handles contribution management for administrators only.
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminContributionController {

    // ==================== Constructor Dependency Injection ==================== //

    // Service responsible for retrieving contribution records.
    private final ContributionService contributionService;

    // Injects ContributionService through the constructor.
    public AdminContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    // ==================== Contribution Management List ==================== //

    // Displays all contributions to the admin management page.
    @GetMapping("/admin/contributions")
    public String contributionManagementPage(Model model) {

        // Retrieve all contribution records from the service.
        List<Contribution> contributions =
                contributionService.getAllContributionsList();

        // Pass the contribution list to the Thymeleaf template.
        model.addAttribute("contributions", contributions);

        // ==================== Total Contributions ==================== //

        // Calculate the total number of contribution records.
        model.addAttribute("totalContributions", contributions.size());

        // ==================== Pending Contributions ==================== //

        // Count contributions with PENDING status.
        long pendingContributions = contributions.stream()
                .filter(contribution ->
                        contribution.getContributionStatus()
                                == Contribution.ContributionStatus.PENDING)
                .count();

        // Pass the pending contribution count to the view.
        model.addAttribute("pendingContributions", pendingContributions);

        // ==================== Paid Contributions ==================== //

        // Count contributions with PAID status.
        long paidContributions = contributions.stream()
                .filter(contribution ->
                        contribution.getContributionStatus()
                                == Contribution.ContributionStatus.PAID)
                .count();

        // Pass the paid contribution count to the view.
        model.addAttribute("paidContributions", paidContributions);

        // ==================== Canceled Contributions ==================== //

        // Count contributions with CANCELED status.
        long canceledContributions = contributions.stream()
                .filter(contribution ->
                        contribution.getContributionStatus()
                                == Contribution.ContributionStatus.CANCELED)
                .count();

        // Pass the canceled contribution count to the view.
        model.addAttribute("canceledContributions", canceledContributions);

        // Return the Thymeleaf template for the admin contributions page.
        return "admin/contributions";
    }



    // ==================== Show Add Contribution Form ==================== //

    // Displays the form for creating a new contribution record.
    @GetMapping("/admin/contributions/new")
    public String showAddContributionForm(Model model) {

        // Create an empty Contribution object to bind form input fields.
        Contribution contribution = new Contribution();

        // Pass the contribution object to the Thymeleaf form.
        model.addAttribute("contribution", contribution);

        // Set edit mode to false because this is a new contribution.
        model.addAttribute("editMode", false);

        // Return the Thymeleaf template for the contribution form.
        return "admin/contribution-form";
    }


    // ==================== Create Contribution ==================== //

    // Handles form submission for creating a new contribution.
    @PostMapping("/admin/contributions")
    public String createContribution(
            @RequestParam String employeeId,
            @Valid @ModelAttribute("contribution") Contribution contribution,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        // ==================== Validate Employee ID ==================== //

        // Ensure the employee ID is provided before processing the form.
        if (employeeId == null || employeeId.trim().isEmpty()) {

            model.addAttribute("errorMessage", "Employee ID is required.");
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("editMode", false);

            // Return to the form and display the validation error.
            return "admin/contribution-form";
        }

        // ==================== Validate Contribution Fields ==================== //

        // Check whether the contribution fields satisfy validation rules.
        if (bindingResult.hasErrors()) {

            model.addAttribute("employeeId", employeeId);
            model.addAttribute("editMode", false);

            // Return to the form so the user can correct invalid fields.
            return "admin/contribution-form";
        }

        try {

            // Create the contribution using the official bank Employee ID.
            contributionService.createContribution(
                    employeeId.trim(),
                    contribution
            );

            // Display a success message after redirecting to the list page.
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Contribution created successfully."
            );

            // Redirect to prevent accidental duplicate form submissions.
            return "redirect:/admin/contributions";

        } catch (DuplicateContributionException exception) {

            // Display the business validation error, such as a duplicate contribution.
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("editMode", false);

            // Return to the form so the user can review and correct the issue.
            return "admin/contribution-form";
        }
    }


    // ==================== View Contribution Details ==================== //

    // Displays the details of a specific contribution record.
    @GetMapping("/admin/contributions/{id}")
    public String viewContributionDetails(
            @PathVariable Long id,
            Model model
    ) {

        // Retrieve the contribution record using its database ID.
        Contribution contribution =
                contributionService.findContributionById(id);

        // Pass the contribution details to the Thymeleaf template.
        model.addAttribute("contribution", contribution);

        // Return the contribution details page.
        return "admin/contribution-details";
    }


    // ==================== Show Edit Contribution Form ==================== //

    // Displays the edit form for an existing contribution.
    @GetMapping("/admin/contributions/{id}/edit")
    public String showEditContributionForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        // Retrieve the contribution record using its database ID.
        Contribution contribution =
                contributionService.findContributionById(id);

        // Allow editing only when the contribution status is PENDING.
        if (contribution.getContributionStatus()
                != Contribution.ContributionStatus.PENDING) {

            // Display an error message if the contribution cannot be edited.
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Only pending contributions can be edited."
            );

            // Redirect to the contribution details page.
            return "redirect:/admin/contributions/" + id;
        }

        // Pass the existing contribution data to the Thymeleaf form.
        model.addAttribute("contribution", contribution);

        // Provide the associated employee's official Employee ID.
        model.addAttribute(
                "employeeId",
                contribution.getEmployee().getEmployeeId()
        );

        // Enable edit mode to distinguish editing from creating.
        model.addAttribute("editMode", true);

        // Return the shared contribution form in edit mode.
        return "admin/contribution-form";
    }



    // ==================== Update Contribution ==================== //

    // Handles form submission for updating an existing contribution.
    @PostMapping("/admin/contributions/{id}/update")
    public String updateContribution(
            @PathVariable Long id,
            @Valid @ModelAttribute("contribution") Contribution contribution,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        // ==================== Validate Contribution Fields ==================== //

        // Check whether the submitted contribution fields contain validation errors.
        if (bindingResult.hasErrors()) {

            // Retrieve the existing record to preserve the associated employee ID.
            Contribution existingContribution =
                    contributionService.findContributionById(id);

            // Pass the employee ID back to the form.
            model.addAttribute(
                    "employeeId",
                    existingContribution.getEmployee().getEmployeeId()
            );

            // Keep the form in edit mode.
            model.addAttribute("editMode", true);

            // Return to the form so the user can correct invalid fields.
            return "admin/contribution-form";
        }

        try {

            // Update the existing contribution using its database ID.
            contributionService.updateContribution(
                    id,
                    contribution
            );

            // Display a success message after the update completes.
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Contribution updated successfully."
            );

            // Redirect to the updated contribution's details page.
            return "redirect:/admin/contributions/" + id;

        } catch (DuplicateContributionException | IllegalStateException exception) {

            // Retrieve the existing record to restore its employee information.
            Contribution existingContribution =
                    contributionService.findContributionById(id);

            // Preserve the employee ID when returning to the edit form.
            model.addAttribute(
                    "employeeId",
                    existingContribution.getEmployee().getEmployeeId()
            );

            // Display the error message returned by the service layer.
            model.addAttribute("errorMessage", exception.getMessage());

            // Keep the form in edit mode after the error.
            model.addAttribute("editMode", true);

            // Return to the form so the user can review the error.
            return "admin/contribution-form";
        }
    }



    // ==================== Mark Contribution Paid ==================== //

    // Handles the action of marking a contribution as paid.
    @PostMapping("/admin/contributions/{id}/pay")
    public String markContributionPaid(
            @PathVariable Long id,
            @RequestParam LocalDate paymentDate,
            RedirectAttributes redirectAttributes
    ) {

        try {

            // Update the contribution status to PAID through the service layer.
            contributionService.markContributionAsPaid(id, paymentDate);

            // Display a success message after the status is updated.
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Contribution marked as paid successfully."
            );

            // Redirect to the contribution details page.
            return "redirect:/admin/contributions/" + id;

        } catch (IllegalStateException exception) {

            // Display the error message if the contribution cannot be marked as paid.
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            // Redirect to the contribution details page to show the error.
            return "redirect:/admin/contributions/" + id;
        }
    }



    // ==================== Cancel Contribution ==================== //

    // Handles the action of canceling an existing contribution.
    @PostMapping("/admin/contributions/{id}/cancel")
    public String cancelContribution(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            // Cancel the contribution through the service layer.
            contributionService.cancelContribution(id);

            // Display a success message after the contribution is canceled.
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Contribution canceled successfully."
            );

            // Redirect to the contribution details page.
            return "redirect:/admin/contributions/" + id;

        } catch (IllegalStateException exception) {

            // Display the error message if the contribution cannot be canceled.
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            // Redirect to the details page to display the error message.
            return "redirect:/admin/contributions/" + id;
        }
    }



}