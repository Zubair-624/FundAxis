package com.fundaxis.repository;

import com.fundaxis.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    // Find a user account by its unique username
    // Used later during login/authentication
    Optional<UserAccount> findByUsername(String username);


    // Check whether a username already exists
    // Used when creating a new user account
    boolean existsByUsername(String username);


    // Find the user account linked to a specific Employee
    // One Employee can have at most one UserAccount, so Optional is appropriate here.
    Optional<UserAccount> findByEmployeeEmployeeId(String employeeId);


    // Check whether an Employee already has a user account
    // Prevents accidentally creating multiple accounts for the same Employee.
    boolean existsByEmployeeEmployeeId(String employeeId);


    // Find all accounts using a specific account status
    // Useful later for:
    // - Admin dashboard
    // - IT Support dashboard
    // - Locked account monitoring
    // - Disabled account monitoring
    List<UserAccount> findByAccountStatus(UserAccount.AccountStatus accountStatus);

}