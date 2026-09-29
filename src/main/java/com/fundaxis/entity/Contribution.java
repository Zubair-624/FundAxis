package com.fundaxis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "contributions",

        // The combination of employee_id + contribution_month must be unique (One employee + one month = one contribution)
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_contribution_employee_month",
                        columnNames = {"employee_id", "contribution_month"}
                )
        }
)
public class Contribution {

    // Database primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// ---------- connecting Employee and Contribution entities ----------
    /// Employee is the parent. Contribution is the child. The contributions.employee_id foreign key connects each Contribution to its Employee

    // One Employee can have MANY Contributions; ONE Employee <--- MANY Contributions; Many Contribution records can be connected to one Employee // Many Contributions belong to ONE Employee
    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    // database connection
    // Store the Employee's ID in the contributions table
    @JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_contribution_employee"))

    // Every Contribution Java object has an Employee associated with it
    private Employee employee;
    /// --------------------------------------------------------------------


    // Contribution amount
    @Column(name = "contribution_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal contributionAmount;

    // Contribution Month
    // Example: 2026-09-01 represents September 2026
    // One employee can have only one contribution per month
    @Column(name = "contribution_month", nullable = false)
    private LocalDate contributionMonth;


    // Actual Payment Date
    // Null while contribution is PENDING
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    /// ---------- Contribution Status ----------

    public enum ContributionStatus {
        PAID,
        PENDING,
        CANCELED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "contribution_status", nullable = false, length = 20)
    private ContributionStatus contributionStatus = ContributionStatus.PAID;

    /// ---------- Audit Information ----------

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated-at")
    private LocalDateTime updatedAt;

}
