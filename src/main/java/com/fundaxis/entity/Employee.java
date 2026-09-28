package com.fundaxis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "employees",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "employeeId"),
                @UniqueConstraint(columnNames = "email")
        }
)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    ///--------------------------------------------------------------------------------------
    // official employee ID, that issued by the National Bank/HR, entered manually by admin
    @NotBlank(message = "Employee ID is required")
    @Column(nullable = false, length = 50)
    private String employeeId;
    ///---------------------------------------------------------------------------------------

    @NotBlank(message = "Full name is required")
    @Column(nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, length = 100)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    @Column(length = 20)
    private String phoneNumber;

    ///---------- enum ---------------------------------

    // Nested Enum
    public enum EmployeeStatus{
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        PENDING
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EmployeeStatus employeeStatus = EmployeeStatus.ACTIVE;

    ///---------- Local Time Setup ------------

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    ///---------- Account Deactivations ----------

    @Column(length = 255)
    private String deactivationReason;

    private LocalDateTime deactivatedAt;

}