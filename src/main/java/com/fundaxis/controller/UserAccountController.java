package com.fundaxis.controller;

import com.fundaxis.dto.CreateUserAccountRequest;
import com.fundaxis.dto.LockUserAccountRequest;
import com.fundaxis.entity.UserAccount;
import com.fundaxis.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fundaxis.dto.ResetPasswordRequest;

import java.util.List;

@RestController
@RequestMapping("/api/user-accounts")
public class UserAccountController {

    private final UserAccountService userAccountService;

    ///---------- Constructor Injection ----------
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;

    }


    ///==================== Create User Account ====================///

    ///---------- Create A Login Account For An Existing Employee ----------
    // Access: ADMIN and IT_SUPPORT
    // Example: POST /api/user-accounts/employee/EMP001
    // The client only provides username and raw password
    // Password encoding and security fields are handled by the service
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<UserAccount> createUserAccount(@PathVariable String employeeId, @Valid @RequestBody CreateUserAccountRequest request) {

        UserAccount userAccount = new UserAccount();

        // Only copy the username from the request
        userAccount.setUsername(request.getUsername());

        UserAccount createdUserAccount = userAccountService.createUserAccount(employeeId, userAccount, request.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserAccount);

    }


    ///==================== Read User Account ====================///

    ///---------- Get All User Accounts ----------
    // Access: ADMIN and IT_SUPPORT (needed for account management)
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @GetMapping
    public ResponseEntity<List<UserAccount>> getAllUserAccounts() {

        return ResponseEntity.ok(userAccountService.getAllUserAccounts());

    }


    ///---------- Get A User Account Using Its Database ID (id) ----------
    // Access: ADMIN and IT_SUPPORT
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @GetMapping("/{id}")
    public ResponseEntity<UserAccount> getUserAccountById(@PathVariable Long id) {

        return ResponseEntity.ok(userAccountService.getUserAccountById(id));

    }


    ///---------- Get A User Account Using Its Unique Username ----------
    // Access: ADMIN and IT_SUPPORT
    // Example: GET /api/user-accounts/username/admin
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @GetMapping("/username/{username}")
    public ResponseEntity<UserAccount> getUserAccountByUsername(@PathVariable String username) {

        return ResponseEntity.ok(userAccountService.getUserAccountByUsername(username));

    }


    ///---------- Get The User Account Linked To An Employee ----------
    // Access: ADMIN and IT_SUPPORT
    // Example: GET /api/user-accounts/employee/EMP001
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<UserAccount> getUserAccountByEmployeeId(@PathVariable String employeeId) {

        return ResponseEntity.ok(userAccountService.getUserAccountByEmployeeId(employeeId));

    }


    ///---------- Get User Accounts By Authentication Status ----------
    // Access: ADMIN and IT_SUPPORT
    // Examples: ACTIVE, PENDING, LOCKED, DISABLED
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @GetMapping("/status/{accountStatus}")
    public ResponseEntity<List<UserAccount>> getUserAccountsByStatus(@PathVariable UserAccount.AccountStatus accountStatus) {

        return ResponseEntity.ok(userAccountService.getUserAccountsByStatus(accountStatus));

    }


    ///==================== User Account Workflow ====================///

    ///---------- Activate A Pending User Account ----------
    // Access: ADMIN and IT_SUPPORT
    // PENDING -> ACTIVE
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserAccount> activateUserAccount(@PathVariable Long id) {

        return ResponseEntity.ok(userAccountService.activateUserAccount(id));

    }


    ///---------- Disable A User Account ----------
    // Access: ADMIN and IT_SUPPORT
    // Used when an account should no longer be used
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<UserAccount> disableUserAccount(@PathVariable Long id) {

        return ResponseEntity.ok(userAccountService.disableUserAccount(id));

    }


    ///---------- Temporarily Lock A User Account ----------
    // Access: ADMIN and IT_SUPPORT
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @PatchMapping("/{id}/lock")
    public ResponseEntity<UserAccount> lockUserAccount(@PathVariable Long id, @Valid @RequestBody LockUserAccountRequest request) {

        return ResponseEntity.ok(userAccountService.lockUserAccount(id, request.getLockedUntil()));

    }


    ///---------- Unlock A Temporarily Locked User Account ----------
    // Access: ADMIN and IT_SUPPORT
    // LOCKED -> ACTIVE
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @PatchMapping("/{id}/unlock")
    public ResponseEntity<UserAccount> unlockUserAccount(@PathVariable Long id) {

        return ResponseEntity.ok(userAccountService.unlockUserAccount(id));

    }


    ///==================== User Account Roles ====================///

    ///---------- Assign A Role To A User Account ----------
    // Access: ADMIN only, because a role change can raise a user's authority level
    // Example: PATCH /api/user-accounts/1/roles/ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/roles/{roleCode}")
    public ResponseEntity<UserAccount> assignRoleToUser(@PathVariable Long id, @PathVariable String roleCode) {

        return ResponseEntity.ok(userAccountService.assignRoleToUser(id, roleCode));

    }


    ///---------- Remove A Role From A User Account ----------
    // Access: ADMIN only, because removing a role changes what a user can do
    // Example: DELETE /api/user-accounts/1/roles/USER
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/roles/{roleCode}")
    public ResponseEntity<UserAccount> removeRoleFromUser(@PathVariable Long id, @PathVariable String roleCode) {

        return ResponseEntity.ok(userAccountService.removeRoleFromUser(id, roleCode));

    }


    ///---------- Reset A User's Password ----------
    // Access: ADMIN and IT_SUPPORT
    // Example: PATCH /api/user-accounts/1/reset-password
    // The password in the request is temporary, the user must change it after the next login
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<UserAccount> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {

        // id -> database ID of the user account whose password is being reset
        // The service checks the confirmation, saves the new hash, and sets mustChangePassword = true
        UserAccount userAccount = userAccountService.resetPassword(id, request.getTemporaryPassword(), request.getConfirmPassword());

        return ResponseEntity.ok(userAccount);

    }


    ///---------- Re-Enable An Administratively Disabled Account ----------
    // Access: ADMIN and IT_SUPPORT
    // Example: PATCH /api/user-accounts/1/enable
    // DISABLED -> ACTIVE
    // Not the same as /activate, which only moves PENDING -> ACTIVE
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    @PatchMapping("/{id}/enable")
    public ResponseEntity<UserAccount> enableUserAccount(@PathVariable Long id) {

        // id -> database ID of the disabled user account
        // The service blocks the request if the account is not DISABLED
        return ResponseEntity.ok(userAccountService.enableUserAccount(id));

    }
}