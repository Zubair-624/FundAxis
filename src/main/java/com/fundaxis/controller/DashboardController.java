package com.fundaxis.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

///========== Primary Admin and Secondary Admin both use the ADMIN dashboard, because both have the same ADMIN authority. We do not need a separate Primary/Secondary Admin dashboard. ==========///

// =========================================================
// DASHBOARD CONTROLLER
//
// Purpose:
// Serves the Thymeleaf dashboard pages for each FundAxis role:
// - ADMIN       -> /admin/dashboard
// - IT_SUPPORT  -> /it-support/dashboard
// - USER        -> /user/dashboard
//
// Serves role-based dashboards with secured access.
// =========================================================
@Controller
public class DashboardController {

    ///========== Redirect an authenticated user to the correct dashboard ==========///

    // Used by /dashboard, which any logged-in user can open
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {

        // ADMIN has the highest redirect priority
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        // IT Support dashboard
        if (hasRole(authentication, "ROLE_IT_SUPPORT")) {
            return "redirect:/it-support/dashboard";
        }

        // Normal employee dashboard
        if (hasRole(authentication, "ROLE_USER")) {
            return "redirect:/user/dashboard";
        }

        // Authenticated account has no supported FundAxis role
        throw new AccessDeniedException("No FundAxis dashboard is available for this account");
    }


    ///========== ADMIN Dashboard ==========///

    // ADMIN dashboard
    // Returns the Thymeleaf template: admin/dashboard
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }


    ///========== IT Support Dashboard ==========///

    // IT Support dashboard
    // Returns the Thymeleaf template: it-support/dashboard
    @PreAuthorize("hasRole('IT_SUPPORT')")
    @GetMapping("/it-support/dashboard")
    public String itSupportDashboard() {
        return "it-support/dashboard";
    }


    ///========== Normal Employee (User) Dashboard ==========///

    // Normal employee dashboard
    // Returns the Thymeleaf template: user/dashboard
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user/dashboard";
    }

    // Check whether the authenticated account has a role
    // Spring stores roles with the "ROLE_" prefix, e.g. ROLE_ADMIN
    private boolean hasRole(Authentication authentication, String role) {
        return authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}