package com.fundaxis.controller;

import com.fundaxis.service.UserAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// =========================================================
// WEB AUTH CONTROLLER
//
// Purpose:
// Serves the Thymeleaf pages used for authentication:
// - Login page
// - Change-password page (forced or voluntary)
//
// IMPORTANT:
// Login itself is NOT handled here.
// POST /login is processed by Spring Security (see SecurityConfig).
// This controller only shows the login page.
// =========================================================
@Controller
public class WebAuthController {

    private final UserAccountService userAccountService;

    // Constructor injection
    public WebAuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    // Show FundAxis login page
    // Public route (permitted in SecurityConfig)
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    // Show forced/self password-change page
    // Any authenticated user can open it, including users with mustChangePassword = true
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "auth/change-password";
    }

    // Process password change from Thymeleaf form
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public String changePassword(
            Authentication authentication,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        try {
            // The username comes from the logged-in session, never from the form,
            // so a user can only change their own password.
            // The service validates the passwords and updates the account.
            userAccountService.changePassword(
                    authentication.getName(),
                    currentPassword,
                    newPassword,
                    confirmPassword);

            // Flash attributes survive one redirect, so the next page can show this message
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");

            // Success: continue to the general dashboard redirect
            return "redirect:/dashboard";

        } catch (IllegalArgumentException exception) {
            // Validation failed (e.g. wrong current password or passwords do not match):
            // show the service's message on the change-password page again
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());

            return "redirect:/change-password";
        }
    }
}