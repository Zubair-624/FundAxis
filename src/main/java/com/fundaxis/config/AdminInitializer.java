package com.fundaxis.config;

// Our entities and repositories
import com.fundaxis.entity.Employee;
import com.fundaxis.entity.Role;
import com.fundaxis.entity.UserAccount;
import com.fundaxis.repository.EmployeeRepository;
import com.fundaxis.repository.RoleRepository;
import com.fundaxis.repository.UserAccountRepository;
// Spring classes
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

///==================== Admin Initializer ====================///
// Creates default accounts at startup (development only)

// Tells Spring this class defines beans
@Configuration
public class AdminInitializer {

    // Used to find the employee for the USER account
    private final EmployeeRepository employeeRepository;

    ///---------- Constructor Injection ----------
    // Spring passes the repository in automatically
    public AdminInitializer(EmployeeRepository employeeRepository) {
        // Save it for later use
        this.employeeRepository = employeeRepository;

    }


    ///==================== Initialize Default Accounts ====================///

    ///---------- Create The Default Accounts At Startup ----------
    // Spring keeps the result of this method
    @Bean
    // Runs once after the application starts
    CommandLineRunner initializeDefaultAccounts(UserAccountRepository userAccountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {

        // Code that runs at startup
        return args -> {

            // =========================================================
            // PRIMARY ADMIN (reset on every startup)
            // =========================================================

            // Find the ADMIN role, stop with an error if missing
            Role adminRole = roleRepository.findByRoleCode("ADMIN").orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            // Load the existing admin, or start a new one
            UserAccount admin = userAccountRepository.findByUsername("admin").orElseGet(UserAccount::new);

            // Login name
            admin.setUsername("admin");

            // Hashed password, reset on every startup (development only)
            admin.setPasswordHash(passwordEncoder.encode("Admin@12345"));

            // Account can log in
            admin.setAccountStatus(UserAccount.AccountStatus.ACTIVE);

            // Clear failed login count
            admin.setFailedLoginAttempts(0);
            // Clear any lock
            admin.setLockedUntil(null);

            // Admin is not forced to change the password
            admin.setMustChangePassword(false);

            // Give the ADMIN role
            admin.setRoles(Set.of(adminRole));

            // Save to the database
            userAccountRepository.save(admin);

            // Show a message in the console
            System.out.println("FundAxis Primary Admin account initialized.");


            // =========================================================
            // IT SUPPORT (created only if missing)
            // =========================================================

            // Find the IT_SUPPORT role, stop with an error if missing
            Role itSupportRole = roleRepository.findByRoleCode("IT_SUPPORT").orElseThrow(() -> new RuntimeException("IT_SUPPORT role not found"));

            // Create the account only if it does not exist
            if (userAccountRepository.findByUsername("itsupport").isEmpty()) {

                // New empty account
                UserAccount itSupport = new UserAccount();

                // Login name
                itSupport.setUsername("itsupport");

                // Hashed password
                itSupport.setPasswordHash(passwordEncoder.encode("ITSupport@69"));

                // Account can log in
                itSupport.setAccountStatus(UserAccount.AccountStatus.ACTIVE);

                // Must change the password at first login
                itSupport.setMustChangePassword(true);

                // Clean security record
                itSupport.setFailedLoginAttempts(0);

                // No lock
                itSupport.setLockedUntil(null);

                // Give the IT_SUPPORT role
                itSupport.setRoles(Set.of(itSupportRole));

                // Save to the database
                userAccountRepository.save(itSupport);

                // Show a message in the console
                System.out.println("Default FundAxis IT Support account created.");

            }


            // =========================================================
            // USER (created only if missing, linked to an employee)
            // =========================================================

            // Find the USER role, stop with an error if missing
            Role userRole = roleRepository.findByRoleCode("USER").orElseThrow(() -> new RuntimeException("USER role not found"));

            // Find employee EMP001, startup fails if it does not exist
            Employee employee = employeeRepository.findByEmployeeId("EMP003")
                    .orElseThrow(() -> new RuntimeException("Employee EMP003 not found"));

            // Create the account only if it does not exist
            if (userAccountRepository.findByUsername("user").isEmpty()) {

                // New empty account
                UserAccount user = new UserAccount();

                // Login name
                user.setUsername("user");

                // Hashed password
                user.setPasswordHash(passwordEncoder.encode("User@12345"));

                // Link to the employee (used by /api/me/...)
                user.setEmployee(employee);

                // Account can log in
                user.setAccountStatus(UserAccount.AccountStatus.ACTIVE);

                // Must change the password at first login
                user.setMustChangePassword(true);

                // Clean security record
                user.setFailedLoginAttempts(0);

                // No lock
                user.setLockedUntil(null);

                // Give the USER role
                user.setRoles(Set.of(userRole));

                // Save to the database
                userAccountRepository.save(user);

                // Show a message in the console
                System.out.println("Default FundAxis USER account created.");

            }

        };

    }
}