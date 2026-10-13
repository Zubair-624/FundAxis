package com.fundaxis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles", uniqueConstraints = {@UniqueConstraint(name = "uk_role_code", columnNames = "role_code")})
public class Role {

    ///---------- Database Primary Key ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    ///---------- Unique System Code Used Internally For Authorization ----------
    // Initial FundAxis roles: ADMIN, IT_SUPPORT, USER
    // Role codes should remain stable because Spring Security and authorization rules will use these values later
    @NotBlank(message = "Role code is required")
    @Size(max = 50, message = "Role code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "Role code must contain only uppercase letters, numbers, and underscores")
    @Column(name = "role_code", nullable = false, length = 50, unique = true)
    private String roleCode;


    ///---------- Human-Readable Role Name ----------
    // Examples: Administrator, IT Support, User
    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must not exceed 100 characters")
    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;


    ///---------- Optional Description Explaining The Purpose Of The Role ----------
    // Example: "Provides full administrative access to FundAxis."
    @Size(max = 500, message = "Role description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;


    ///---------- Indicates Whether This Is A Core FundAxis System Role ----------
    // Core roles such as ADMIN, IT_SUPPORT and USER should normally be protected from accidental deletion or unsafe modification
    // Custom roles created in the future can use: systemRole = false
    @NotNull(message = "System role flag is required")
    @Column(name = "system_role", nullable = false)
    private Boolean systemRole = false;


    ///---------- Indicates Whether The Role Is Currently Available For Use ----------
    // true  -> role can be assigned and used
    // false -> role is disabled
    // Disabling is safer than deleting a role that may already
    // be referenced by user accounts or audit history
    @NotNull(message = "Role active status is required")
    @Column(name = "active", nullable = false)
    private Boolean active = true;


    ///---------- Record Creation Timestamp ----------
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;


    ///---------- Record Last Update Timestamp ----------
    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
}