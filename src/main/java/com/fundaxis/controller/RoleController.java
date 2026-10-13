package com.fundaxis.controller;

import com.fundaxis.entity.Role;
import com.fundaxis.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

///==================== Role Controller ====================///
// Access: ADMIN only
// The @PreAuthorize on the class applies to every endpoint below,
// because roles control what every FundAxis user is allowed to do
@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    ///---------- Constructor Injection ----------
    public RoleController(RoleService roleService) {
        this.roleService = roleService;

    }


    ///==================== Create Role ====================///

    ///---------- Create A New Custom Role ----------
    // systemRole and active are controlled by the service
    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {

        Role createdRole = roleService.createRole(role);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);

    }


    ///==================== Read Role ====================///

    ///---------- Get All Roles ----------
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {

        return ResponseEntity.ok(roleService.getAllRoles());

    }


    ///---------- Get A Role Using Its Database ID (id) ----------
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {

        return ResponseEntity.ok(roleService.getRoleById(id));

    }


    ///---------- Get A Role Using Its Unique Role Code ----------
    // Examples: ADMIN, IT_SUPPORT, USER
    @GetMapping("/code/{roleCode}")
    public ResponseEntity<Role> getRoleByCode(@PathVariable String roleCode) {

        return ResponseEntity.ok(roleService.getRoleByCode(roleCode));

    }


    ///---------- Get All Active Roles ----------
    @GetMapping("/active")
    public ResponseEntity<List<Role>> getActiveRoles() {

        return ResponseEntity.ok(roleService.getActiveRoles());

    }


    ///---------- Get All Inactive Roles ----------
    @GetMapping("/inactive")
    public ResponseEntity<List<Role>> getInactiveRoles() {

        return ResponseEntity.ok(roleService.getInactiveRoles());

    }


    ///---------- Get All Protected FundAxis System Roles ----------
    // Expected: ADMIN, IT_SUPPORT, USER
    @GetMapping("/system")
    public ResponseEntity<List<Role>> getSystemRoles() {

        return ResponseEntity.ok(roleService.getSystemRoles());

    }


    ///---------- Get All Custom/Non-System Roles ----------
    @GetMapping("/custom")
    public ResponseEntity<List<Role>> getCustomRoles() {

        return ResponseEntity.ok(roleService.getCustomRoles());

    }


    ///==================== Update Role ====================///

    ///---------- Update Role Information ----------
    // System role codes remain protected by RoleService
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @Valid @RequestBody Role updatedRole) {

        return ResponseEntity.ok(roleService.updateRole(id, updatedRole));

    }


    ///==================== Role Workflow ====================///

    ///---------- Activate An Inactive Role ----------
    // INACTIVE -> ACTIVE
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Role> activateRole(@PathVariable Long id) {

        return ResponseEntity.ok(roleService.activateRole(id));

    }


    ///---------- Deactivate An Active Custom Role ----------
    // ACTIVE -> INACTIVE
    // Core FundAxis system roles cannot be deactivated
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Role> deactivateRole(@PathVariable Long id) {

        return ResponseEntity.ok(roleService.deactivateRole(id));

    }
}