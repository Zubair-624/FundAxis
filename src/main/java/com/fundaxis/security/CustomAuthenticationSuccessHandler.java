package com.fundaxis.security;

import com.fundaxis.entity.UserAccount;
import com.fundaxis.repository.UserAccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserAccountRepository userAccountRepository;

    // Constructor injection
    public CustomAuthenticationSuccessHandler(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    // Decide where the authenticated user should go
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();

        UserAccount userAccount = userAccountRepository
                .findByUsername(username)
                .orElse(null);

        // Defensive fallback.
        //
        // This should normally never happen because authentication
        // already succeeded using the same UserAccount.
        if (userAccount == null) {
            response.sendRedirect("/login?error");
            return;
        }

        // Force temporary-password users to change password first
        if (Boolean.TRUE.equals(userAccount.getMustChangePassword())) {
            response.sendRedirect("/change-password");
            return;
        }

        // ADMIN has the highest redirect priority
        if (hasRole(authentication, "ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
            return;
        }

        // IT Support dashboard
        if (hasRole(authentication, "ROLE_IT_SUPPORT")) {
            response.sendRedirect("/it-support/dashboard");
            return;
        }

        // Normal employee dashboard
        if (hasRole(authentication, "ROLE_USER")) {
            response.sendRedirect("/user/dashboard");
            return;
        }

        // Authenticated account has no supported FundAxis role
        response.sendRedirect("/login?unauthorized");
    }

    // Check whether Authentication contains a specific role
    private boolean hasRole(Authentication authentication, String role) {
        return authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}