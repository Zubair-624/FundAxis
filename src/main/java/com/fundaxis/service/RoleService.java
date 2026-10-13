package com.fundaxis.service;

import com.fundaxis.entity.Role;
import com.fundaxis.exception.DuplicateRoleException;
import com.fundaxis.exception.RoleNotFoundException;
import com.fundaxis.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    ///---------- Constructor Injection ----------
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;

    }


    ///==================== Create Role ====================///

    ///---------- Create A New FundAxis Role ----------
    public Role createRole(Role role) {

        // Normalize the role code before checking or saving
        // Example: admin -> ADMIN, it_support -> IT_SUPPORT
        role.setRoleCode(normalizeRoleCode(role.getRoleCode()));

        // Prevent duplicate role codes
        if (roleRepository.existsByRoleCode(role.getRoleCode())) {

            throw new DuplicateRoleException("Role code already exists: " + role.getRoleCode());

        }

        // New custom roles should not be able to declare themselves
        // as protected FundAxis system roles through normal API requests
        // Core system roles such as ADMIN, IT_SUPPORT and USER
        // will be created separately by the application/bootstrap logic
        role.setSystemRole(false);

        // New roles start as active
        role.setActive(true);

        return roleRepository.save(role);

    }


    ///==================== Read Role ====================///

    ///---------- Find A Role Using The Database Primary Key ----------
    public Role getRoleById(Long id) {

        return roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + id));

    }


    ///---------- Find A Role Using Its Unique Role Code ----------
    public Role getRoleByCode(String roleCode) {

        String normalizedRoleCode = normalizeRoleCode(roleCode);

        return roleRepository.findByRoleCode(normalizedRoleCode).orElseThrow(() -> new RoleNotFoundException("Role not found with code: " + normalizedRoleCode));

    }


    ///---------- Return All Roles ----------
    public List<Role> getAllRoles() {

        return roleRepository.findAll();

    }


    ///---------- Return All Active Roles ----------
    public List<Role> getActiveRoles() {

        return roleRepository.findByActiveTrue();

    }


    ///---------- Return All Inactive Roles ----------
    public List<Role> getInactiveRoles() {

        return roleRepository.findByActiveFalse();

    }


    ///---------- Return All Protected FundAxis System Roles ----------
    // Expected: ADMIN, IT_SUPPORT, USER
    public List<Role> getSystemRoles() {

        return roleRepository.findBySystemRoleTrue();

    }


    ///---------- Return All Custom/Non-System Roles ----------
    public List<Role> getCustomRoles() {

        return roleRepository.findBySystemRoleFalse();

    }


    ///==================== Update Role ====================///

    ///---------- Update The Editable Information Of An Existing Role ----------
    public Role updateRole(Long id, Role updatedRole) {

        Role existingRole = getRoleById(id);

        // System role codes must remain stable because they will
        // later be used by Spring Security and RBAC
        if (Boolean.TRUE.equals(existingRole.getSystemRole())) {

            // For protected system roles, keep the original role code
            updatedRole.setRoleCode(existingRole.getRoleCode());

        } else {

            String normalizedRoleCode = normalizeRoleCode(updatedRole.getRoleCode());

            // Prevent another role from using the requested code
            if (!existingRole.getRoleCode().equals(normalizedRoleCode) && roleRepository.existsByRoleCode(normalizedRoleCode)) {

                throw new DuplicateRoleException("Role code already exists: " + normalizedRoleCode);

            }

            existingRole.setRoleCode(normalizedRoleCode);

        }

        // Update human-readable role information
        existingRole.setRoleName(updatedRole.getRoleName());

        existingRole.setDescription(updatedRole.getDescription());

        // Do not allow normal update requests to modify:
        // id, systemRole, active, createdAt, updatedAt
        // Role activation/deactivation uses dedicated methods

        return roleRepository.save(existingRole);

    }


    ///==================== Role Workflow ====================///

    ///---------- Activate An Inactive Role ----------
    public Role activateRole(Long id) {

        Role role = getRoleById(id);

        // Prevent repeated activation
        if (Boolean.TRUE.equals(role.getActive())) {

            throw new IllegalStateException("Role is already active");

        }

        role.setActive(true);

        return roleRepository.save(role);

    }


    ///---------- Deactivate An Active Role ----------
    public Role deactivateRole(Long id) {

        Role role = getRoleById(id);

        // Core FundAxis system roles should not be disabled
        // through normal role-management operations
        if (Boolean.TRUE.equals(role.getSystemRole())) {

            throw new IllegalStateException("System roles cannot be deactivated");

        }

        // Prevent repeated deactivation
        if (Boolean.FALSE.equals(role.getActive())) {

            throw new IllegalStateException("Role is already inactive");

        }

        role.setActive(false);

        return roleRepository.save(role);

    }


    ///==================== Helper Methods ====================///

    ///---------- Normalize A Role Code Into The Format Used By FundAxis ----------
    // Examples: "admin" -> "ADMIN", " Admin " -> "ADMIN", "it_support" -> "IT_SUPPORT"
    private String normalizeRoleCode(String roleCode) {

        if (roleCode == null || roleCode.isBlank()) {

            throw new IllegalArgumentException("Role code is required");

        }

        return roleCode.trim().toUpperCase(Locale.ROOT);

    }
}