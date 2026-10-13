package com.fundaxis.security;

import com.fundaxis.entity.Employee;
import com.fundaxis.entity.UserAccount;
import com.fundaxis.exception.UserAccountNotFoundException;
import com.fundaxis.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserAccountRepository userAccountRepository;

    ///---------- Constructor Injection ----------
    public CurrentUserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;

    }


    ///==================== Current User ====================///

    ///---------- Get The Currently Authenticated FundAxis UserAccount ----------
    public UserAccount getCurrentUserAccount() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {

            throw new IllegalStateException("No authenticated user found");

        }

        // The username comes from the login (HTTP Basic), never from the request URL or body
        String username = authentication.getName();

        return userAccountRepository.findByUsername(username).orElseThrow(() -> new UserAccountNotFoundException("Authenticated user account not found"));

    }


    ///---------- Get The Employee Linked To The Currently Authenticated Account ----------
    public Employee getCurrentEmployee() {

        UserAccount userAccount = getCurrentUserAccount();

        // Special accounts (such as a bootstrap admin) may have no linked employee
        if (userAccount.getEmployee() == null) {

            throw new IllegalStateException("Authenticated user account is not linked to an employee");

        }

        return userAccount.getEmployee();

    }


    ///---------- Get The Official Employee ID Of The Authenticated Employee ----------
    public String getCurrentEmployeeId() {

        return getCurrentEmployee().getEmployeeId();

    }
}