package com.fundaxis.security;

import com.fundaxis.service.UserAccountService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

///==================== Authentication Event Listener ====================///
// Listens to Spring Security login events and updates the user account security fields
// (failed login attempts, automatic locking, last login time)
@Component
public class AuthenticationEventListener {

    private final UserAccountService userAccountService;

    ///---------- Constructor Injection ----------
    public AuthenticationEventListener(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;

    }


    ///==================== Login Events ====================///

    ///---------- Handle Successful Authentication ----------
    // Reset failed attempts and update last login after successful authentication
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {

        String username = event.getAuthentication().getName();

        userAccountService.recordSuccessfulLogin(username);

    }


    ///---------- Handle Incorrect Username/Password ----------
    // Count only incorrect username/password authentication failures
    // Locked or disabled accounts raise different events, so they are not counted here
    @EventListener
    public void handleBadCredentials(AuthenticationFailureBadCredentialsEvent event) {

        String username = event.getAuthentication().getName();

        userAccountService.recordFailedLoginAttempt(username);

    }
}