package com.fundaxis.controller;

import com.fundaxis.entity.UserAccount;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fundaxis.dto.ChangePasswordRequest;
import com.fundaxis.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserAccountService userAccountService;

    public AuthenticationController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    ///==================== Current User ====================///

    ///---------- Return Information About The Currently Authenticated FundAxis User ----------
    // Access: any authenticated user
    // The frontend calls this right after login to learn who the user is, which roles they have,
    // and whether they must change their password first
    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserResponse> getCurrentUser(Authentication authentication) {

        // Get the authenticated user's roles (ROLE_ADMIN -> ADMIN, ROLE_USER -> USER)
        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(authority -> authority.startsWith("ROLE_")).map(authority -> authority.substring(5)).toList();

        // Get the full FundAxis user account (needed for the mustChangePassword flag)
        UserAccount userAccount = userAccountService.getUserAccountByUsername(authentication.getName());

        // Build the authentication response
        AuthenticatedUserResponse response = new AuthenticatedUserResponse(authentication.getName(), roles, Boolean.TRUE.equals(userAccount.getMustChangePassword()));

        return ResponseEntity.ok(response);

    }



    ///==================== Response Model ====================///

    ///---------- Response Returned To The Frontend After Successful Authentication ----------
    // Example: { "username": "admin", "roles": ["ADMIN"] }
    public record AuthenticatedUserResponse(String username, List<String> roles, boolean mustChangePassword) {

    }


    // Allow any authenticated user to change their own password
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        userAccountService.changePassword(
                authentication.getName(),
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );


        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }
}