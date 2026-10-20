package com.fundaxis.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.fundaxis.service.UserAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UserAccountService userAccountService;
    
    public LoginController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }


    // Show FundAxis login page
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    // Show FundAxis password recovery page
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    // Show FundAxis change password page
    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "change-password";
    }


    /**
     * Handles the change-password form submission (POST /change-password).
     * Only logged-in users can reach this endpoint.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public String changePassword(
            Authentication authentication,          // Currently logged-in user (provided by Spring Security)
            @RequestParam String currentPassword,   // Old password, used to verify the user's identity
            @RequestParam String newPassword,       // Desired new password
            @RequestParam String confirmPassword,   // Must match newPassword (validated in the service)
            RedirectAttributes redirectAttributes   // Carries one-time flash messages across the redirect
    ) {

        try {

            // Validation (current password check, confirm match, etc.) lives in the service.
            // It throws IllegalArgumentException with a user-friendly message if anything fails.
            userAccountService.changePassword(
                    authentication.getName(),
                    currentPassword,
                    newPassword,
                    confirmPassword
            );

            // Success: show a one-time message on the dashboard
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Password changed successfully."
            );

            return "redirect:/dashboard";

        } catch (IllegalArgumentException exception) {

            // Failure: send the user back to the form with the validation message
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/change-password";
        }
    }



}
