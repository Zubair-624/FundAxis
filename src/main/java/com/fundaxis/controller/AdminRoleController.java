package com.fundaxis.controller;

import com.fundaxis.entity.Role;
import com.fundaxis.service.RoleService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;


/**
 * AdminRoleController
 *
 * Thymeleaf MVC controller for ADMIN role management.
 *
 * REST API:
 *     /api/roles/**
 *
 * Admin Web UI:
 *     /admin/roles/**
 */
@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoleController {


    // ============================================================
    // SERVICE
    // ============================================================

    private final RoleService roleService;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public AdminRoleController(
            RoleService roleService
    ) {

        this.roleService = roleService;
    }


    // ============================================================
    // 1. ROLE LIST PAGE
    // ============================================================

    /**
     * Display all FundAxis roles.
     *
     * Supports:
     * - keyword search
     * - active/inactive filter
     * - system/custom filter
     * - summary counts
     */
    @GetMapping
    public String showRoleManagementPage(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            String roleType,

            Model model
    ) {

        List<Role> allRoles =
                roleService.getAllRoles();


        // ========================================================
        // SUMMARY COUNTS
        // ========================================================

        long totalRoles =
                allRoles.size();


        long activeRoles =
                allRoles.stream()
                        .filter(role ->
                                Boolean.TRUE.equals(
                                        role.getActive()
                                )
                        )
                        .count();


        long inactiveRoles =
                allRoles.stream()
                        .filter(role ->
                                Boolean.FALSE.equals(
                                        role.getActive()
                                )
                        )
                        .count();


        long systemRoles =
                allRoles.stream()
                        .filter(role ->
                                Boolean.TRUE.equals(
                                        role.getSystemRole()
                                )
                        )
                        .count();


        long customRoles =
                allRoles.stream()
                        .filter(role ->
                                Boolean.FALSE.equals(
                                        role.getSystemRole()
                                )
                        )
                        .count();


        model.addAttribute(
                "totalRoles",
                totalRoles
        );


        model.addAttribute(
                "activeRoles",
                activeRoles
        );


        model.addAttribute(
                "inactiveRoles",
                inactiveRoles
        );


        model.addAttribute(
                "systemRoles",
                systemRoles
        );


        model.addAttribute(
                "customRoles",
                customRoles
        );


        // ========================================================
        // SEARCH + FILTER
        // ========================================================

        List<Role> filteredRoles =
                allRoles.stream()

                        // ----------------------------------------
                        // Keyword search
                        // ----------------------------------------
                        .filter(role -> {

                            if (keyword == null
                                    || keyword.trim().isEmpty()) {

                                return true;
                            }


                            String search =
                                    keyword
                                            .trim()
                                            .toLowerCase();


                            boolean codeMatches =
                                    role.getRoleCode() != null
                                            && role
                                            .getRoleCode()
                                            .toLowerCase()
                                            .contains(search);


                            boolean nameMatches =
                                    role.getRoleName() != null
                                            && role
                                            .getRoleName()
                                            .toLowerCase()
                                            .contains(search);


                            boolean descriptionMatches =
                                    role.getDescription() != null
                                            && role
                                            .getDescription()
                                            .toLowerCase()
                                            .contains(search);


                            return codeMatches
                                    || nameMatches
                                    || descriptionMatches;
                        })


                        // ----------------------------------------
                        // Active / Inactive filter
                        // ----------------------------------------
                        .filter(role -> {

                            if (status == null
                                    || status.isBlank()) {

                                return true;
                            }


                            if ("ACTIVE".equalsIgnoreCase(status)) {

                                return Boolean.TRUE.equals(
                                        role.getActive()
                                );
                            }


                            if ("INACTIVE".equalsIgnoreCase(status)) {

                                return Boolean.FALSE.equals(
                                        role.getActive()
                                );
                            }


                            return true;
                        })


                        // ----------------------------------------
                        // System / Custom filter
                        // ----------------------------------------
                        .filter(role -> {

                            if (roleType == null
                                    || roleType.isBlank()) {

                                return true;
                            }


                            if ("SYSTEM".equalsIgnoreCase(roleType)) {

                                return Boolean.TRUE.equals(
                                        role.getSystemRole()
                                );
                            }


                            if ("CUSTOM".equalsIgnoreCase(roleType)) {

                                return Boolean.FALSE.equals(
                                        role.getSystemRole()
                                );
                            }


                            return true;
                        })


                        // System roles first, then role code.
                        .sorted(
                                Comparator
                                        .comparing(
                                                (Role role) ->
                                                        Boolean.TRUE.equals(
                                                                role.getSystemRole()
                                                        )
                                        )
                                        .reversed()
                                        .thenComparing(
                                                role ->
                                                        role.getRoleCode() != null
                                                                ? role.getRoleCode()
                                                                : ""
                                        )
                        )

                        .toList();


        model.addAttribute(
                "roles",
                filteredRoles
        );


        model.addAttribute(
                "keyword",
                keyword
        );


        model.addAttribute(
                "selectedStatus",
                status
        );


        model.addAttribute(
                "selectedRoleType",
                roleType
        );


        return "admin/roles";
    }


    // ============================================================
    // 2. SHOW CREATE ROLE FORM
    // ============================================================

    /**
     * Display the form for creating a custom role.
     */
    @GetMapping("/new")
    public String showCreateRoleForm(
            Model model
    ) {

        Role role =
                new Role();


        // New roles are custom roles.
        role.setSystemRole(false);

        // RoleService will also enforce active=true.
        role.setActive(true);


        model.addAttribute(
                "role",
                role
        );


        model.addAttribute(
                "editMode",
                false
        );


        return "admin/role-form";
    }


    // ============================================================
    // 3. CREATE ROLE
    // ============================================================

    /**
     * Create a new custom FundAxis role.
     */
    @PostMapping
    public String createRole(

            @Valid
            @ModelAttribute("role")
            Role role,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "editMode",
                    false
            );


            return "admin/role-form";
        }


        try {

            Role createdRole =
                    roleService.createRole(role);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Role "
                            + createdRole.getRoleCode()
                            + " created successfully."
            );


            return "redirect:/admin/roles/"
                    + createdRole.getId();


        } catch (RuntimeException exception) {

            model.addAttribute(
                    "editMode",
                    false
            );


            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            return "admin/role-form";
        }
    }


    // ============================================================
    // 4. ROLE DETAILS
    // ============================================================

    /**
     * Display complete information for one role.
     */
    @GetMapping("/{id}")
    public String viewRoleDetails(

            @PathVariable
            Long id,

            Model model
    ) {

        Role role =
                roleService.getRoleById(id);


        model.addAttribute(
                "role",
                role
        );


        model.addAttribute(
                "systemRole",
                Boolean.TRUE.equals(
                        role.getSystemRole()
                )
        );


        model.addAttribute(
                "customRole",
                Boolean.FALSE.equals(
                        role.getSystemRole()
                )
        );


        model.addAttribute(
                "activeRole",
                Boolean.TRUE.equals(
                        role.getActive()
                )
        );


        model.addAttribute(
                "inactiveRole",
                Boolean.FALSE.equals(
                        role.getActive()
                )
        );


        return "admin/role-details";
    }


    // ============================================================
    // 5. SHOW EDIT ROLE FORM
    // ============================================================

    /**
     * Display the edit form.
     *
     * RoleService protects system role codes.
     */
    @GetMapping("/{id}/edit")
    public String showEditRoleForm(

            @PathVariable
            Long id,

            Model model
    ) {

        Role role =
                roleService.getRoleById(id);


        model.addAttribute(
                "role",
                role
        );


        model.addAttribute(
                "editMode",
                true
        );


        return "admin/role-form";
    }


    // ============================================================
    // 6. UPDATE ROLE
    // ============================================================

    /**
     * Update role information.
     *
     * For system roles:
     * - roleCode remains protected by RoleService
     *
     * For custom roles:
     * - roleCode may be changed if unique
     */
    @PostMapping("/{id}/update")
    public String updateRole(

            @PathVariable
            Long id,

            @Valid
            @ModelAttribute("role")
            Role role,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            role.setId(id);


            model.addAttribute(
                    "editMode",
                    true
            );


            return "admin/role-form";
        }


        try {

            Role updatedRole =
                    roleService.updateRole(
                            id,
                            role
                    );


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Role updated successfully."
            );


            return "redirect:/admin/roles/"
                    + updatedRole.getId();


        } catch (RuntimeException exception) {

            role.setId(id);


            model.addAttribute(
                    "editMode",
                    true
            );


            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            return "admin/role-form";
        }
    }


    // ============================================================
    // 7. ACTIVATE ROLE
    // ============================================================

    /**
     * INACTIVE -> ACTIVE
     */
    @PostMapping("/{id}/activate")
    public String activateRole(

            @PathVariable
            Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            Role role =
                    roleService.activateRole(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Role "
                            + role.getRoleCode()
                            + " activated successfully."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return "redirect:/admin/roles/" + id;
    }


    // ============================================================
    // 8. DEACTIVATE ROLE
    // ============================================================

    /**
     * ACTIVE -> INACTIVE
     *
     * RoleService blocks deactivation of system roles.
     */
    @PostMapping("/{id}/deactivate")
    public String deactivateRole(

            @PathVariable
            Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            Role role =
                    roleService.deactivateRole(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Role "
                            + role.getRoleCode()
                            + " deactivated successfully."
            );


        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }


        return "redirect:/admin/roles/" + id;
    }
}