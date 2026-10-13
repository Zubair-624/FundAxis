package com.fundaxis.security;

import com.fundaxis.entity.UserAccount;
import com.fundaxis.repository.UserAccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// =========================================================
// MUST CHANGE PASSWORD FILTER
//
// Purpose:
// Force users with a temporary password (mustChangePassword = true)
// to change their password before using the rest of FundAxis.
//
// How it works:
// - Runs once per request, AFTER authentication (see SecurityConfig:
//   addFilterAfter BasicAuthenticationFilter for /api/**, and
//   UsernamePasswordAuthenticationFilter for web pages).
// - Anonymous / unauthenticated requests are ignored here.
// - A small list of routes stays open so the user can log in,
//   log out and actually change the password.
// - Every other request is blocked:
//     /api/**       -> 403 Forbidden with a plain-text message
//     browser pages -> redirect to /change-password
//
// IMPORTANT:
// This filter does NOT authenticate anyone.
// Spring Security has already done that before this filter runs.
// =========================================================
@Component
public class MustChangePasswordFilter extends OncePerRequestFilter {

    private final UserAccountRepository userAccountRepository;

    // Constructor injection
    public MustChangePasswordFilter(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    // Runs once for every incoming request
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Get the user that Spring Security has already authenticated (if any)
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        // No authenticated user yet.
        //
        // Covers: no authentication, not authenticated,
        // or an anonymous (not logged-in) visitor.
        // Nothing to check, so continue the normal filter chain.
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            filterChain.doFilter(request, response);
            return;
        }

        // The path the user is trying to open, e.g. /admin/dashboard
        String requestUri = request.getRequestURI();

        // Allow routes required to complete password change.
        //
        // Without this, the user could never reach the
        // change-password page and would be stuck in a redirect loop.
        if (isAllowedWhilePasswordChangeRequired(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Load the account from the database to read the
        // current mustChangePassword flag
        UserAccount userAccount = userAccountRepository
                .findByUsername(authentication.getName())
                .orElse(null);

        // The user must change the password before doing anything else
        if (userAccount != null
                && Boolean.TRUE.equals(userAccount.getMustChangePassword())) {

            // REST API response (Postman / API clients cannot follow a page redirect)
            if (requestUri.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("text/plain");
                response.getWriter().write("Password change is required before accessing FundAxis");
                return;
            }

            // Browser / Thymeleaf response: send the user to the change-password page
            response.sendRedirect("/change-password");
            return;
        }

        // Password is already changed (or account not found): continue normally
        filterChain.doFilter(request, response);
    }

    // Routes that must remain available while the
    // user is required to change their password
    private boolean isAllowedWhilePasswordChangeRequired(String requestUri) {
        return requestUri.equals("/login")                        // Login page / login processing
                || requestUri.equals("/logout")                   // User can always log out
                || requestUri.equals("/change-password")          // Web password change page
                || requestUri.equals("/api/auth/me")              // API: who am I
                || requestUri.equals("/api/auth/change-password") // API: password change
                || requestUri.startsWith("/css/")                 // Static resources needed
                || requestUri.startsWith("/js/")                  // to render the pages
                || requestUri.startsWith("/images/")
                || requestUri.equals("/favicon.ico")
                || requestUri.equals("/error");                   // Spring error page
    }
}