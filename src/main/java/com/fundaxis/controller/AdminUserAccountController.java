package com.fundaxis.controller;

import com.fundaxis.entity.Role;
import com.fundaxis.entity.UserAccount;
import com.fundaxis.service.UserAccountService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


/**
 * AdminUserAccountController
 *
 * Handles the Thymeleaf-based ADMIN user-account management interface.
 *
 * IMPORTANT:
 * This controller is separate from UserAccountController.
 *
 * UserAccountController:
 *      /api/user-accounts/**
 *      REST API
 *
 * AdminUserAccountController:
 *      /admin/user-accounts/**
 *      ADMIN web interface
 */
@Controller
@RequestMapping("/admin/user-accounts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserAccountController {


    // ============================================================
    // SERVICE DEPENDENCY
    // ============================================================ //

    private final UserAccountService userAccountService;


    // ============================================================
    // CONSTRUCTOR INJECTION
    // ============================================================ //

    public AdminUserAccountController(
            UserAccountService userAccountService
    ) {

        this.userAccountService = userAccountService;
    }


    // ============================================================
    // 1. USER ACCOUNT MANAGEMENT LIST
    // ============================================================ //

    /**
     * Display all FundAxis user accounts.
     *
     * Supports:
     * - Search
     * - Account status filtering
     * - Summary cards
     */
    @GetMapping
    public String showUserAccountManagementPage(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String accountStatus,

            Model model
    ) {

        // Load all accounts.
        List<UserAccount> allUserAccounts =
                userAccountService.getAllUserAccounts();


        // ========================================================
        // SUMMARY CARDS
        // ======================================================== //

        long totalAccounts =
                allUserAccounts.size();


        long pendingAccounts =
                allUserAccounts.stream()
                        .filter(account ->
                                account.getAccountStatus()
                                        == UserAccount.AccountStatus.PENDING
                        )
                        .count();


        long activeAccounts =
                allUserAccounts.stream()
                        .filter(account ->
                                account.getAccountStatus()
                                        == UserAccount.AccountStatus.ACTIVE
                        )
                        .count();


        long lockedAccounts =
                allUserAccounts.stream()
                        .filter(account ->
                                account.getAccountStatus()
                                        == UserAccount.AccountStatus.LOCKED
                        )
                        .count();


        long disabledAccounts =
                allUserAccounts.stream()
                        .filter(account ->
                                account.getAccountStatus()
                                        == UserAccount.AccountStatus.DISABLED
                        )
                        .count();


        long passwordChangeRequiredAccounts =
                allUserAccounts.stream()
                        .filter(account ->
                                Boolean.TRUE.equals(
                                        account.getMustChangePassword()
                                )
                        )
                        .count();


        model.addAttribute(
                "totalAccounts",
                totalAccounts
        );


        model.addAttribute(
                "pendingAccounts",
                pendingAccounts
        );


        model.addAttribute(
                "activeAccounts",
                activeAccounts
        );


        model.addAttribute(
                "lockedAccounts",
                lockedAccounts
        );


        model.addAttribute(
                "disabledAccounts",
                disabledAccounts
        );


        model.addAttribute(
                "passwordChangeRequiredAccounts",
                passwordChangeRequiredAccounts
        );


        // ========================================================
        // SEARCH AND FILTER
        // ======================================================== //

        List<UserAccount> filteredUserAccounts =
                allUserAccounts.stream()

                        // --------------------------------------------
                        // Search
                        // --------------------------------------------
                        .filter(account -> {

                            if (keyword == null
                                    || keyword.trim().isEmpty()) {

                                return true;
                            }


                            String searchValue =
                                    keyword.trim()
                                            .toLowerCase();


                            // Search username.
                            boolean usernameMatches =
                                    account.getUsername() != null
                                            && account
                                            .getUsername()
                                            .toLowerCase()
                                            .contains(searchValue);


                            // Search Employee ID.
                            boolean employeeIdMatches =
                                    account.getEmployee() != null
                                            && account
                                            .getEmployee()
                                            .getEmployeeId() != null
                                            && account
                                            .getEmployee()
                                            .getEmployeeId()
                                            .toLowerCase()
                                            .contains(searchValue);


                            // Search Employee Name.
                            boolean employeeNameMatches =
                                    account.getEmployee() != null
                                            && account
                                            .getEmployee()
                                            .getFullName() != null
                                            && account
                                            .getEmployee()
                                            .getFullName()
                                            .toLowerCase()
                                            .contains(searchValue);


                            // Search assigned role code.
                            boolean roleMatches =
                                    account.getRoles() != null
                                            && account.getRoles()
                                            .stream()
                                            .anyMatch(role ->
                                                    role.getRoleCode() != null
                                                            && role
                                                            .getRoleCode()
                                                            .toLowerCase()
                                                            .contains(
                                                                    searchValue
                                                            )
                                            );


                            return usernameMatches
                                    || employeeIdMatches
                                    || employeeNameMatches
                                    || roleMatches;
                        })


                        // --------------------------------------------
                        // Account Status Filter
                        // --------------------------------------------
                        .filter(account -> {

                            if (accountStatus == null
                                    || accountStatus.isBlank()) {

                                return true;
                            }


                            return account.getAccountStatus() != null
                                    && account
                                    .getAccountStatus()
                                    .name()
                                    .equalsIgnoreCase(
                                            accountStatus
                                    );
                        })


                        // Newest account first.
                        .sorted(
                                Comparator.comparing(
                                                UserAccount::getCreatedAt,
                                                Comparator.nullsLast(
                                                        Comparator.naturalOrder()
                                                )
                                        )
                                        .reversed()
                        )


                        .toList();


        model.addAttribute(
                "userAccounts",
                filteredUserAccounts
        );


        // Preserve filters.
        model.addAttribute(
                "keyword",
                keyword
        );


        model.addAttribute(
                "selectedAccountStatus",
                accountStatus
        );


        // Status values for dropdown.
        model.addAttribute(
                "accountStatuses",
                UserAccount.AccountStatus.values()
        );


        return "admin/user-accounts";
    }


    // ============================================================
    // 2. SHOW CREATE USER ACCOUNT FORM
    // ============================================================ //

    /**
     * Display the user-account creation page.
     *
     * The account is created for an existing employee.
     */
    @GetMapping("/new")
    public String showCreateUserAccountForm(
            Model model
    ) {

        model.addAttribute(
                "employeeId",
                ""
        );


        model.addAttribute(
                "username",
                ""
        );


        return "admin/user-account-form";
    }


    // ============================================================
    // 3. CREATE USER ACCOUNT
    // ============================================================ //

    /**
     * Create a login account for an existing employee.
     *
     * New account defaults are handled by UserAccountService:
     *
     * PENDING
     * failedLoginAttempts = 0
     * mustChangePassword = true
     */
    @PostMapping
    public String createUserAccount(

            @RequestParam
            String employeeId,

            @RequestParam
            String username,

            @RequestParam
            String temporaryPassword,

            @RequestParam
            String confirmPassword,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        // ========================================================
        // Basic Form Validation
        // ======================================================== //

        if (employeeId == null
                || employeeId.trim().isEmpty()) {

            return returnCreateFormWithError(
                    model,
                    employeeId,
                    username,
                    "Employee ID is required."
            );
        }


        if (username == null
                || username.trim().isEmpty()) {

            return returnCreateFormWithError(
                    model,
                    employeeId,
                    username,
                    "Username is required."
            );
        }


        if (temporaryPassword == null
                || temporaryPassword.isBlank()) {

            return returnCreateFormWithError(
                    model,
                    employeeId,
                    username,
                    "Temporary password is required."
            );
        }


        if (confirmPassword == null
                || confirmPassword.isBlank()) {

            return returnCreateFormWithError(
                    model,
                    employeeId,
                    username,
                    "Password confirmation is required."
            );
        }


        if (!temporaryPassword.equals(confirmPassword)) {

            return returnCreateFormWithError(
                    model,
                    employeeId,
                    username,
                    "Temporary password and confirmation do not match."
            );
        }


        try {

            UserAccount userAccount =
                    new UserAccount();


            userAccount.setUsername(
                    username.trim()
            );


            UserAccount createdUserAccount =
                    userAccountService.createUserAccount(
                            employeeId.trim(),
                            userAccount,
                            temporaryPassword
                    );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account created successfully. "
                            + "Assign the required role and activate the account."
            );


            return "redirect:/admin/user-accounts/"
                    + createdUserAccount.getId();


        } catch (RuntimeException exception) {

            return returnCreateFormWithError(
                    model,
                    employeeId,
                    username,
                    exception.getMessage()
            );
        }
    }


    // ============================================================
    // 4. VIEW USER ACCOUNT DETAILS
    // ============================================================ //

    /**
     * Display complete account/security information.
     */
    @GetMapping("/{id}")
    public String viewUserAccountDetails(

            @PathVariable
            Long id,

            Authentication authentication,

            Model model
    ) {

        UserAccount userAccount =
                userAccountService
                        .getUserAccountById(id);


        // Current logged-in admin username.
        String currentUsername =
                authentication != null
                        ? authentication.getName()
                        : null;


        boolean ownAccount =
                currentUsername != null
                        && userAccount.getUsername() != null
                        && currentUsername.equalsIgnoreCase(
                        userAccount.getUsername()
                );


        boolean hasAdminRole =
                hasRole(
                        userAccount,
                        "ADMIN"
                );


        boolean hasITSupportRole =
                hasRole(
                        userAccount,
                        "IT_SUPPORT"
                );


        boolean hasUserRole =
                hasRole(
                        userAccount,
                        "USER"
                );


        model.addAttribute(
                "userAccount",
                userAccount
        );


        model.addAttribute(
                "currentUsername",
                currentUsername
        );


        model.addAttribute(
                "ownAccount",
                ownAccount
        );


        model.addAttribute(
                "hasAdminRole",
                hasAdminRole
        );


        model.addAttribute(
                "hasITSupportRole",
                hasITSupportRole
        );


        model.addAttribute(
                "hasUserRole",
                hasUserRole
        );


        return "admin/user-account-details";
    }


    // ============================================================
    // 5. ACTIVATE PENDING ACCOUNT
    // ============================================================ //

    /**
     * PENDING -> ACTIVE
     */
    @PostMapping("/{id}/activate")
    public String activateUserAccount(

            @PathVariable
            Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            userAccountService.activateUserAccount(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account activated successfully."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return redirectToDetails(id);
    }


    // ============================================================
    // 6. DISABLE ACCOUNT
    // ============================================================ //

    /**
     * Administratively disables an account.
     *
     * The logged-in ADMIN is prevented from disabling
     * their own account through this interface.
     */
    @PostMapping("/{id}/disable")
    public String disableUserAccount(

            @PathVariable
            Long id,

            Authentication authentication,

            RedirectAttributes redirectAttributes
    ) {

        try {

            UserAccount targetAccount =
                    userAccountService
                            .getUserAccountById(id);


            preventSelfSecurityAction(
                    targetAccount,
                    authentication,
                    "You cannot disable your own administrator account."
            );


            userAccountService
                    .disableUserAccount(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account disabled successfully."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return redirectToDetails(id);
    }


    // ============================================================
    // 7. ENABLE DISABLED ACCOUNT
    // ============================================================ //

    /**
     * DISABLED -> ACTIVE
     */
    @PostMapping("/{id}/enable")
    public String enableUserAccount(

            @PathVariable
            Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            userAccountService
                    .enableUserAccount(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account enabled successfully."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return redirectToDetails(id);
    }


    // ============================================================
    // 8. MANUALLY LOCK ACCOUNT
    // ============================================================ //

    /**
     * Temporarily lock an account.
     *
     * The administrator provides the lock expiration date/time.
     */
    @PostMapping("/{id}/lock")
    public String lockUserAccount(

            @PathVariable
            Long id,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime lockedUntil,

            Authentication authentication,

            RedirectAttributes redirectAttributes
    ) {

        try {

            UserAccount targetAccount =
                    userAccountService
                            .getUserAccountById(id);


            preventSelfSecurityAction(
                    targetAccount,
                    authentication,
                    "You cannot lock your own administrator account."
            );


            userAccountService
                    .lockUserAccount(
                            id,
                            lockedUntil
                    );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account locked successfully until "
                            + lockedUntil
                            + "."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return redirectToDetails(id);
    }


    // ============================================================
    // 9. UNLOCK ACCOUNT
    // ============================================================ //

    /**
     * LOCKED -> ACTIVE
     *
     * Also resets failed-login attempts and lockedUntil.
     */
    @PostMapping("/{id}/unlock")
    public String unlockUserAccount(

            @PathVariable
            Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            userAccountService
                    .unlockUserAccount(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account unlocked successfully."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return redirectToDetails(id);
    }


    // ============================================================
    // 10. RESET USER PASSWORD
    // ============================================================ //

    /**
     * ADMIN reset of another user's password.
     *
     * After reset:
     *
     * mustChangePassword = true
     *
     * If the account was LOCKED:
     *
     * LOCKED -> ACTIVE
     *
     * The administrator should use Change Password
     * instead of resetting their own password here.
     */
    @PostMapping("/{id}/reset-password")
    public String resetUserPassword(

            @PathVariable
            Long id,

            @RequestParam
            String temporaryPassword,

            @RequestParam
            String confirmPassword,

            Authentication authentication,

            RedirectAttributes redirectAttributes
    ) {

        try {

            UserAccount targetAccount =
                    userAccountService
                            .getUserAccountById(id);


            // Prevent admin from using administrative reset
            // against their own account.
            //
            // Primary Admin and Secondary Admin can still
            // reset each other's passwords.
            preventSelfSecurityAction(
                    targetAccount,
                    authentication,
                    "Use Change Password to change your own password. "
                            + "Administrative password reset is intended "
                            + "for another user account."
            );


            if (temporaryPassword == null
                    || temporaryPassword.isBlank()) {

                throw new IllegalArgumentException(
                        "Temporary password is required."
                );
            }


            if (confirmPassword == null
                    || confirmPassword.isBlank()) {

                throw new IllegalArgumentException(
                        "Password confirmation is required."
                );
            }


            userAccountService.resetPassword(
                    id,
                    temporaryPassword,
                    confirmPassword
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Password reset successfully. "
                            + "The user must change the temporary password "
                            + "after the next successful login."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return redirectToDetails(id);
    }


    // ============================================================
    // 11. ASSIGN ROLE
    // ============================================================ //

    /**
     * Assign a role to the selected user account.
     *
     * Supported role records are controlled by the Role table.
     *
     * Example:
     *
     * ADMIN
     * IT_SUPPORT
     * USER
     */
    @PostMapping("/{id}/roles/assign")
    public String assignRoleToUser(

            @PathVariable
            Long id,

            @RequestParam
            String roleCode,

            RedirectAttributes redirectAttributes
    ) {

        try {

            if (roleCode == null
                    || roleCode.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Role is required."
                );
            }


            userAccountService.assignRoleToUser(
                    id,
                    roleCode.trim()
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Role "
                            + roleCode.trim().toUpperCase()
                            + " assigned successfully."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return redirectToDetails(id);
    }


    // ============================================================
    // 12. REMOVE ROLE
    // ============================================================ //

    /**
     * Remove a role from a user account.
     *
     * Important protection:
     * the logged-in ADMIN cannot remove ADMIN from themselves.
     */
    @PostMapping("/{id}/roles/{roleCode}/remove")
    public String removeRoleFromUser(

            @PathVariable
            Long id,

            @PathVariable
            String roleCode,

            Authentication authentication,

            RedirectAttributes redirectAttributes
    ) {

        try {

            UserAccount targetAccount =
                    userAccountService
                            .getUserAccountById(id);


            boolean ownAccount =
                    isCurrentAuthenticatedAccount(
                            targetAccount,
                            authentication
                    );


            if (ownAccount
                    && "ADMIN".equalsIgnoreCase(
                    roleCode
            )) {

                throw new IllegalStateException(
                        "You cannot remove the ADMIN role "
                                + "from your own logged-in account."
                );
            }


            userAccountService.removeRoleFromUser(
                    id,
                    roleCode
            );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Role "
                            + roleCode.toUpperCase()
                            + " removed successfully."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return redirectToDetails(id);
    }


    // ============================================================
    // HELPER — CREATE FORM ERROR
    // ============================================================ //

    /**
     * Rebuild the account creation form after an error.
     *
     * Password values are intentionally NOT returned to the page.
     */
    private String returnCreateFormWithError(

            Model model,

            String employeeId,

            String username,

            String errorMessage
    ) {

        model.addAttribute(
                "employeeId",
                employeeId
        );


        model.addAttribute(
                "username",
                username
        );


        model.addAttribute(
                "errorMessage",
                errorMessage
        );


        return "admin/user-account-form";
    }


    // ============================================================
    // HELPER — CHECK ROLE
    // ============================================================ //

    /**
     * Check whether an account currently contains a particular role.
     */
    private boolean hasRole(

            UserAccount userAccount,

            String roleCode
    ) {

        if (userAccount == null
                || userAccount.getRoles() == null
                || roleCode == null) {

            return false;
        }


        return userAccount
                .getRoles()
                .stream()
                .filter(role -> role != null)
                .map(Role::getRoleCode)
                .filter(code -> code != null)
                .anyMatch(code ->
                        code.equalsIgnoreCase(
                                roleCode
                        )
                );
    }


    // ============================================================
    // HELPER — DETERMINE CURRENT ACCOUNT
    // ============================================================ //

    /**
     * Returns true when the target account is the account
     * currently authenticated in Spring Security.
     */
    private boolean isCurrentAuthenticatedAccount(

            UserAccount targetAccount,

            Authentication authentication
    ) {

        if (targetAccount == null
                || targetAccount.getUsername() == null
                || authentication == null
                || authentication.getName() == null) {

            return false;
        }


        return targetAccount
                .getUsername()
                .equalsIgnoreCase(
                        authentication.getName()
                );
    }


    // ============================================================
    // HELPER — PREVENT DANGEROUS SELF ACTION
    // ============================================================ //

    /**
     * Prevents the logged-in administrator from accidentally
     * locking, disabling or administratively resetting themselves.
     */
    private void preventSelfSecurityAction(

            UserAccount targetAccount,

            Authentication authentication,

            String errorMessage
    ) {

        if (isCurrentAuthenticatedAccount(
                targetAccount,
                authentication
        )) {

            throw new IllegalStateException(
                    errorMessage
            );
        }
    }


    // ============================================================
    // HELPER — DETAILS REDIRECT
    // ============================================================ //

    private String redirectToDetails(
            Long id
    ) {

        return "redirect:/admin/user-accounts/"
                + id;
    }
}