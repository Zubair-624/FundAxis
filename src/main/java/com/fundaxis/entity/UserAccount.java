package com.fundaxis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "user_accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_account_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_user_account_employee", columnNames = "employee_id")
        }
)
public class UserAccount {

    ///---------- Database Primary Key ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    ///---------- Optional Link Between This Login Account And An Employee Record ----------
    // One employee can have at most one normal FundAxis user account
    // This relationship is optional because special system accounts, such as a bootstrap administrator, may exist without an Employee record
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true, foreignKey = @ForeignKey(name = "fk_user_account_employee"))
    private Employee employee;


    ///---------- Unique Username Used To Log In To FundAxis ----------
    // Example: admin, support01, EMP001
    // Username must be unique because two accounts cannot share the same login identity
    @NotBlank(message = "Username is required")
    @Column(name = "username", nullable = false, length = 100, unique = true)
    private String username;


    ///---------- Stores The Encoded/Hashed Password ----------
    // IMPORTANT: this field must NEVER contain the user's plain-text password
    // Example stored value: $2a$10$...
    // Password hashing will be handled later by Spring Security
    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;


    ///---------- Authentication Account Lifecycle ----------
    // PENDING  -> account created but not fully activated yet
    // ACTIVE   -> account can normally log in
    // LOCKED   -> account temporarily blocked, usually after failed logins
    // DISABLED -> account administratively disabled
    public enum AccountStatus {
        PENDING,
        ACTIVE,
        LOCKED,
        DISABLED
    }


    ///---------- Current Authentication Status Of The User Account ----------
    // This is separate from Employee.EmployeeStatus
    // Example: Employee may still be ACTIVE, while their UserAccount is temporarily LOCKED
    @NotNull(message = "Account status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus = AccountStatus.PENDING;


    ///---------- Number Of Consecutive Failed Login Attempts ----------
    // Later authentication logic can increase this value whenever an incorrect password is entered
    // After reaching the configured limit, the account can be changed to LOCKED
    @NotNull(message = "Failed login attempts are required")
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;


    ///---------- Date And Time Until Which The Account Remains Locked ----------
    // Null when the account is not temporarily locked
    // Example: account locked at 13:00, lockedUntil = 13:30
    @Column(name = "locked_until")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime lockedUntil;


    ///---------- Date And Time Of The User's Most Recent Successful Login ----------
    // Useful later for the Admin dashboard, IT Support dashboard, and security monitoring
    // Null before the first successful login
    @Column(name = "last_login_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime lastLoginAt;


    ///---------- Indicates Whether The User Must Change Their Password After The Next Successful Login ----------
    // Example: admin creates account with temporary password -> mustChangePassword = true
    // User changes password -> mustChangePassword = false
    @NotNull(message = "Password change requirement is required")
    @Column(name = "must_change_password", nullable = false)
    private Boolean mustChangePassword = true;


    ///---------- Stores When The Password Was Last Changed ----------
    // Useful later for password reset tracking, security auditing, and session invalidation
    // May initially be null depending on how the account is created
    @Column(name = "password_changed_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime passwordChangedAt;


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


    // Roles assigned to this user account.
    //
    // A user account can have one or more roles.
    //
    // Examples:
    // admin     -> ADMIN
    // support01 -> IT_SUPPORT
    // employee1 -> USER
    //
    // Roles are stored through the user_roles join table.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_account_id",
                    foreignKey = @ForeignKey(
                            name = "fk_user_role_user_account"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    foreignKey = @ForeignKey(
                            name = "fk_user_role_role"
                    )
            ),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_user_role",
                            columnNames = {
                                    "user_account_id",
                                    "role_id"
                            }
                    )
            }
    )
    private Set<Role> roles = new HashSet<>();
    
}