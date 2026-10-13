package com.fundaxis.service;

import com.fundaxis.entity.Employee;
import com.fundaxis.entity.UserAccount;
import com.fundaxis.exception
        .DuplicateUserAccountException;
import com.fundaxis.exception.EmployeeNotFoundException;
import com.fundaxis.exception.RoleNotFoundException;
import com.fundaxis.exception.UserAccountNotFoundException;
import com.fundaxis.repository.EmployeeRepository;
import com.fundaxis.repository.RoleRepository;
import com.fundaxis.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fundaxis.entity.Role;
import com.fundaxis.repository.RoleRepository;
import com.fundaxis.exception.RoleNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    ///----------------------------------------------------------------------
    // Maximum number of incorrect passwords before automatic locking
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    // Automatic lock duration
    private static final long AUTO_LOCK_MINUTES = 15;
    ///----------------------------------------------------------------------

    ///---------- Constructor Injection ----------
    public UserAccountService(UserAccountRepository userAccountRepository,
                              EmployeeRepository employeeRepository,
                              PasswordEncoder passwordEncoder,
                              RoleRepository roleRepository) {

        this.userAccountRepository = userAccountRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;

    }


    ///==================== Create User Account ====================///

    ///---------- Create A Login Account For An Existing Employee ----------
    public UserAccount createUserAccount(String employeeId, UserAccount userAccount, String rawPassword) {

        // Find the employee using the official Employee ID
        Employee employee = employeeRepository.findByEmployeeId(employeeId).orElseThrow(() -> new EmployeeNotFoundException("Employee not found with Employee ID: " + employeeId));

        // Prevent multiple login accounts for the same employee
        if (userAccountRepository.existsByEmployeeEmployeeId(employeeId)) {
            throw new DuplicateUserAccountException("User account already exists for Employee ID: " + employeeId);

        }

        // Prevent duplicate username
        if (userAccountRepository.existsByUsername(userAccount.getUsername())) {
            throw new DuplicateUserAccountException("Username already exists: " + userAccount.getUsername());

        }

        // Raw password must be provided
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");

        }

        // Link this login account to the employee
        userAccount.setEmployee(employee);

        // Encode the password before saving it
        // Plain-text passwords must never be stored in the database
        userAccount.setPasswordHash(passwordEncoder.encode(rawPassword));

        // New accounts start as PENDING
        // Account activation will be handled using a dedicated action
        userAccount.setAccountStatus(UserAccount.AccountStatus.PENDING);

        // Initialize security-controlled fields
        userAccount.setFailedLoginAttempts(0);
        userAccount.setLockedUntil(null);
        userAccount.setLastLoginAt(null);

        // User must change the temporary password after first login
        userAccount.setMustChangePassword(true);

        // No password change has occurred yet
        userAccount.setPasswordChangedAt(null);

        return userAccountRepository.save(userAccount);

    }


    ///==================== Read User Account ====================///

    ///---------- Find A User Account Using The Database Primary Key ----------
    public UserAccount getUserAccountById(Long id) {

        return userAccountRepository.findById(id).orElseThrow(() -> new UserAccountNotFoundException("User account not found with ID: " + id));

    }


    ///---------- Find A User Account Using Its Unique Username ----------
    public UserAccount getUserAccountByUsername(String username) {

        return userAccountRepository.findByUsername(username).orElseThrow(() -> new UserAccountNotFoundException("User account not found with username: " + username));

    }


    ///---------- Find The Login Account Belonging To An Employee ----------
    public UserAccount getUserAccountByEmployeeId(String employeeId) {

        return userAccountRepository.findByEmployeeEmployeeId(employeeId).orElseThrow(() -> new UserAccountNotFoundException("User account not found for Employee ID: " + employeeId));

    }


    ///---------- Return All User Accounts ----------
    public List<UserAccount> getAllUserAccounts() {

        return userAccountRepository.findAll();

    }


    ///---------- Return Accounts By Authentication Status ----------
    // Example: ACTIVE, PENDING, LOCKED, DISABLED
    public List<UserAccount> getUserAccountsByStatus(UserAccount.AccountStatus accountStatus) {

        return userAccountRepository.findByAccountStatus(accountStatus);

    }


    ///==================== User Account Workflow ====================///

    ///---------- Activate A Pending User Account ----------
    public UserAccount activateUserAccount(Long id) {

        UserAccount userAccount = getUserAccountById(id);

        // Only PENDING accounts can be activated normally
        if (userAccount.getAccountStatus() != UserAccount.AccountStatus.PENDING) {
            throw new IllegalStateException("Only PENDING user accounts can be activated");

        }

        userAccount.setAccountStatus(UserAccount.AccountStatus.ACTIVE);

        return userAccountRepository.save(userAccount);

    }


    ///---------- Disable A User Account ----------
    public UserAccount disableUserAccount(Long id) {

        UserAccount userAccount = getUserAccountById(id);

        // Prevent repeatedly disabling the same account
        if (userAccount.getAccountStatus() == UserAccount.AccountStatus.DISABLED) {
            throw new IllegalStateException("User account is already disabled");

        }

        userAccount.setAccountStatus(UserAccount.AccountStatus.DISABLED);

        // Remove any temporary lock information
        userAccount.setLockedUntil(null);

        return userAccountRepository.save(userAccount);

    }


    ///---------- Lock A User Account Temporarily ----------
    // This will later be used automatically when too many incorrect login attempts occur
    public UserAccount lockUserAccount(Long id, LocalDateTime lockedUntil) {

        UserAccount userAccount = getUserAccountById(id);

        // Disabled accounts should not be changed to LOCKED
        if (userAccount.getAccountStatus() == UserAccount.AccountStatus.DISABLED) {
            throw new IllegalStateException("Disabled user account cannot be locked");

        }

        if (lockedUntil == null) {
            throw new IllegalArgumentException("Lock expiration time is required");

        }

        if (!lockedUntil.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Lock expiration time must be in the future");

        }

        userAccount.setAccountStatus(UserAccount.AccountStatus.LOCKED);

        userAccount.setLockedUntil(lockedUntil);

        return userAccountRepository.save(userAccount);

    }


    ///---------- Unlock A Temporarily Locked User Account ----------
    // This can later be used by Admin or IT Support
    public UserAccount unlockUserAccount(Long id) {

        UserAccount userAccount = getUserAccountById(id);

        // Only LOCKED accounts can be unlocked
        if (userAccount.getAccountStatus() != UserAccount.AccountStatus.LOCKED) {
            throw new IllegalStateException("Only LOCKED user accounts can be unlocked");

        }

        userAccount.setAccountStatus(UserAccount.AccountStatus.ACTIVE);

        // Clear failed-login security information
        userAccount.setFailedLoginAttempts(0);
        userAccount.setLockedUntil(null);

        return userAccountRepository.save(userAccount);

    }



    ///==================== User Account Roles ====================///

    ///---------- Assign A Role To A User Account ----------
    // Example: user ID = 1, roleCode = ADMIN
    // The role must already exist and must be active
    // A user account cannot be given the same role twice
    public UserAccount assignRoleToUser(Long userAccountId, String roleCode) {

        // Find the user account using its database ID
        UserAccount userAccount = getUserAccountById(userAccountId);

        // Find the role using its code (the code is trimmed and converted to uppercase first)
        // Example: " admin " -> "ADMIN"
        Role role = roleRepository.findByRoleCode(roleCode.trim().toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Role not found with code: " + roleCode));

        // Disabled roles cannot be assigned
        if (Boolean.FALSE.equals(role.getActive())) {

            throw new IllegalStateException("Inactive role cannot be assigned");

        }

        // Prevent assigning the same role more than once
        if (userAccount.getRoles().contains(role)) {

            throw new IllegalStateException("User account already has role: " + role.getRoleCode());

        }

        // Add the role to the user account's role list
        userAccount.getRoles().add(role);

        // Save the updated user account
        return userAccountRepository.save(userAccount);

    }


    ///---------- Remove A Role From A User Account ----------
    // Example: user ID = 1, roleCode = USER
    // The user account must currently have the role
    public UserAccount removeRoleFromUser(Long userAccountId, String roleCode) {

        // Find the user account using its database ID
        UserAccount userAccount = getUserAccountById(userAccountId);

        // Find the role using its code (the code is trimmed and converted to uppercase first)
        // Example: " user " -> "USER"
        Role role = roleRepository.findByRoleCode(roleCode.trim().toUpperCase()).orElseThrow(() -> new RoleNotFoundException("Role not found with code: " + roleCode));

        // Check whether the user actually has this role
        if (!userAccount.getRoles().contains(role)) {

            throw new IllegalStateException("User account does not have role: " + role.getRoleCode());

        }

        // Remove the role from the user account's role list
        userAccount.getRoles().remove(role);

        // Save the updated user account
        return userAccountRepository.save(userAccount);

    }


    ///==================== User Account Password ====================///

    ///---------- Change The Password Of An Authenticated User ----------
    // The user must know the current password
    // The new password must be confirmed and must be different from the current one
    public UserAccount changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {

        // Find the user account using the login username
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElseThrow(() -> new UserAccountNotFoundException("User account not found: " + username));

        // Verify the current password against the stored BCrypt hash
        if (!passwordEncoder.matches(currentPassword, userAccount.getPasswordHash())) {

            throw new IllegalArgumentException("Current password is incorrect");

        }

        // New password and confirmation must match
        if (!newPassword.equals(confirmPassword)) {

            throw new IllegalArgumentException("New password and confirmation do not match");

        }

        // Prevent reusing the current password
        if (passwordEncoder.matches(newPassword, userAccount.getPasswordHash())) {

            throw new IllegalArgumentException("New password must be different from the current password");

        }

        // Encode the new password before saving it
        // Plain-text passwords must never be stored in the database
        userAccount.setPasswordHash(passwordEncoder.encode(newPassword));

        // The user has chosen their own password, so the forced-change flag is cleared
        userAccount.setMustChangePassword(false);

        // Record when the password was changed (useful for security auditing)
        userAccount.setPasswordChangedAt(LocalDateTime.now());

        // Reset the failed-login counter after a successful change
        userAccount.setFailedLoginAttempts(0);

        return userAccountRepository.save(userAccount);

    }


    ///---------- Reset A User's Password ----------
    // Intended for ADMIN or IT_SUPPORT
    // A reset password is treated as temporary
    // The user must change it after successfully authenticating
    public UserAccount resetPassword(Long userAccountId, String temporaryPassword, String confirmPassword) {

        // Find the user account using its database ID
        UserAccount userAccount = userAccountRepository.findById(userAccountId).orElseThrow(() -> new UserAccountNotFoundException("User account not found: " + userAccountId));

        // Temporary password and confirmation must match
        if (!temporaryPassword.equals(confirmPassword)) {

            throw new IllegalArgumentException("Temporary password and confirmation do not match");

        }

        // Encode the temporary password before saving it
        // Plain-text passwords must never be stored in the database
        userAccount.setPasswordHash(passwordEncoder.encode(temporaryPassword));

        // Force the user to create a private password at the next login
        userAccount.setMustChangePassword(true);

        // Record when the password was changed (useful for security auditing)
        userAccount.setPasswordChangedAt(LocalDateTime.now());

        // Clear failed authentication state
        userAccount.setFailedLoginAttempts(0);
        userAccount.setLockedUntil(null);

        // Password reset unlocks a LOCKED account,
        // but does not automatically activate PENDING or DISABLED accounts
        if (userAccount.getAccountStatus() == UserAccount.AccountStatus.LOCKED) {

            userAccount.setAccountStatus(UserAccount.AccountStatus.ACTIVE);

        }

        return userAccountRepository.save(userAccount);

    }


    ///---------- Re-Enable An Administratively Disabled Account ----------
    // DISABLED -> ACTIVE
    // This is the opposite of disableUserAccount
    public UserAccount enableUserAccount(Long id) {

        // Find the user account using its database ID
        UserAccount userAccount = userAccountRepository.findById(id).orElseThrow(() -> new UserAccountNotFoundException("User account not found: " + id));

        // Only DISABLED accounts can be enabled
        if (userAccount.getAccountStatus() != UserAccount.AccountStatus.DISABLED) {

            throw new IllegalStateException("Only DISABLED accounts can be enabled: " + id);

        }

        userAccount.setAccountStatus(UserAccount.AccountStatus.ACTIVE);

        // Clear failed authentication state so the user starts with a clean record
        userAccount.setFailedLoginAttempts(0);
        userAccount.setLockedUntil(null);

        return userAccountRepository.save(userAccount);

    }



    ///==================== Failed Login Handling ====================///

    ///---------- Record An Incorrect Password Attempt ----------
    // Only ACTIVE accounts participate in automatic failed-login locking
    // After too many failures the account is locked for a limited time
    public void recordFailedLoginAttempt(String username) {

        // If the username does not exist, do nothing (this also avoids revealing which usernames are real)
        userAccountRepository.findByUsername(username).ifPresent(userAccount -> {

            // Do not modify PENDING, LOCKED or DISABLED accounts
            if (userAccount.getAccountStatus() != UserAccount.AccountStatus.ACTIVE) {

                return;

            }

            // Count this failed attempt
            int failedAttempts = userAccount.getFailedLoginAttempts() + 1;

            userAccount.setFailedLoginAttempts(failedAttempts);

            // Automatically lock after too many failures
            if (failedAttempts >= MAX_FAILED_LOGIN_ATTEMPTS) {

                userAccount.setAccountStatus(UserAccount.AccountStatus.LOCKED);

                // The lock lasts for AUTO_LOCK_MINUTES minutes
                userAccount.setLockedUntil(LocalDateTime.now().plusMinutes(AUTO_LOCK_MINUTES));

            }

            userAccountRepository.save(userAccount);

        });

    }



    ///---------- Record Successful Authentication ----------
    // Called after a user logs in successfully
    // This is the opposite of recordFailedLoginAttempt
    public void recordSuccessfulLogin(String username) {

        // If the username does not exist, do nothing
        userAccountRepository.findByUsername(username).ifPresent(userAccount -> {

            // A successful login clears the failed-login counter
            userAccount.setFailedLoginAttempts(0);

            // Remember when the user last logged in (used by admin and security dashboards)
            userAccount.setLastLoginAt(LocalDateTime.now());

            userAccountRepository.save(userAccount);

        });

    }


}