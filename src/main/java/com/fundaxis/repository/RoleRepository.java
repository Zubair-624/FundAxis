package com.fundaxis.repository;

import com.fundaxis.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find a role using its unique system code.
    //
    // Examples:
    // ADMIN
    // IT_SUPPORT
    // USER
    //
    // Optional is used because the requested role may not exist.
    Optional<Role> findByRoleCode(String roleCode);


    // Check whether a role code already exists.
    //
    // Used during role creation to prevent duplicate role codes.
    boolean existsByRoleCode(String roleCode);


    // Find all currently active roles.
    //
    // Useful later when displaying roles that can be assigned
    // to a FundAxis user account.
    List<Role> findByActiveTrue();


    // Find all disabled/inactive roles.
    //
    // Useful for Admin role management and auditing.
    List<Role> findByActiveFalse();


    // Find all core FundAxis system roles.
    //
    // Expected initial system roles:
    // ADMIN
    // IT_SUPPORT
    // USER
    List<Role> findBySystemRoleTrue();


    // Find all custom/non-system roles.
    //
    // These may be introduced later if FundAxis requires
    // additional business-specific roles.
    List<Role> findBySystemRoleFalse();
}