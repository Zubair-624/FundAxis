package com.fundaxis.security;

import com.fundaxis.entity.Role;
import com.fundaxis.entity.UserAccount;
import com.fundaxis.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    ///---------- Constructor Injection ----------
    public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;

    }


    ///==================== Load User For Authentication ====================///

    ///---------- Load A FundAxis User Account Using The Login Username ----------
    // Spring Security automatically calls this method
    // whenever HTTP Basic Authentication needs to verify a user
    ///==================== Load User For Authentication ====================///

    ///---------- Load A FundAxis User Account Using The Login Username ----------
    // Spring Security automatically calls this method
    // whenever HTTP Basic Authentication needs to verify a user
    // @Transactional is needed because this method can save the account (auto-unlock)
    // and reads the lazy roles list
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Find the user account from the FundAxis database
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        // Automatically unlock an account after the temporary lock expires
        // This must run before the status checks below, so an expired lock is cleared first
        if (userAccount.getAccountStatus() == UserAccount.AccountStatus.LOCKED
                && userAccount.getLockedUntil() != null
                && !userAccount.getLockedUntil().isAfter(LocalDateTime.now())) {

            userAccount.setAccountStatus(UserAccount.AccountStatus.ACTIVE);

            userAccount.setFailedLoginAttempts(0);

            userAccount.setLockedUntil(null);

            userAccountRepository.save(userAccount);

        }

        // Only ACTIVE accounts are considered enabled
        // PENDING and DISABLED accounts cannot authenticate
        boolean enabled = userAccount.getAccountStatus() == UserAccount.AccountStatus.ACTIVE;

        // LOCKED accounts are prevented from authenticating
        boolean accountNonLocked = userAccount.getAccountStatus() != UserAccount.AccountStatus.LOCKED;

        // Convert FundAxis roles into Spring Security authorities
        // Only active roles are used, so a deactivated role stops giving access immediately
        // Example: ADMIN -> ROLE_ADMIN, IT_SUPPORT -> ROLE_IT_SUPPORT, USER -> ROLE_USER
        List<SimpleGrantedAuthority> authorities = userAccount.getRoles().stream().filter(role -> Boolean.TRUE.equals(role.getActive())).map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode())).toList();

        // Convert our FundAxis UserAccount into Spring Security UserDetails
        // passwordHash contains the BCrypt encoded password
        return User.builder()
                .username(userAccount.getUsername())
                .password(userAccount.getPasswordHash())
                .authorities(authorities)
                .disabled(!enabled)
                .accountLocked(!accountNonLocked)
                .build();

    }
}