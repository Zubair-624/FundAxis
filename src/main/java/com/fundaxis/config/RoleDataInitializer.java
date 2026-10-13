package com.fundaxis.config;

import com.fundaxis.entity.Role;
import com.fundaxis.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleDataInitializer {

    ///==================== Initialize System Roles ====================///

    ///---------- Create The Core FundAxis System Roles When The Application Starts ----------
    // The roles are created only if they do not already exist,
    // so restarting the application will not create duplicates
    @Bean
    public CommandLineRunner initializeSystemRoles(RoleRepository roleRepository) {

        return args -> {

            createSystemRoleIfMissing(roleRepository, "ADMIN", "Administrator", "Full administrative access to FundAxis.");

            createSystemRoleIfMissing(roleRepository, "IT_SUPPORT", "IT Support", "Maintains user accounts and supports the FundAxis system.");

            createSystemRoleIfMissing(roleRepository, "USER", "User", "Standard FundAxis user account for employees.");

        };

    }


    ///==================== Helper Methods ====================///

    ///---------- Create A Protected System Role Only When It Does Not Already Exist ----------
    private void createSystemRoleIfMissing(RoleRepository roleRepository, String roleCode, String roleName, String description) {

        // Do nothing when the role already exists
        if (roleRepository.existsByRoleCode(roleCode)) {

            return;

        }

        Role role = new Role();

        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setDescription(description);

        // Core FundAxis roles are protected system roles
        role.setSystemRole(true);

        // Core roles start as active
        role.setActive(true);

        roleRepository.save(role);

    }
}