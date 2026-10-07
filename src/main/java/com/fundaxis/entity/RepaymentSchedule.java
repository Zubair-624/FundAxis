package com.fundaxis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "repayment_schedules", uniqueConstraints = {@UniqueConstraint(name = "uk_repayment_schedule_loan_installment", columnNames = {"loan_id", "installment_number"})})
public class RepaymentSchedule {

    ///---------- Database Primary Key ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    ///---------- Many LoanRepayment Schedule Records Can Belong To One Loan ----------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false, foreignKey = @ForeignKey(name = "fk_repayment_schedule_loan"))
    private Loan loan;


    ///---------- Sequential Installment Number For The Loan ----------
    @NotNull(message = "Installment number is required")
    @Min(value = 1, message = "Installment number must be at least 1")
    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;


    ///---------- Date On Which The Installment Becomes Due ----------
    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;


    ///---------- Principal Portion Of The Installment ----------
    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "0.00", message = "Principal amount cannot be negative")
    @Column(name = "principal_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal principalAmount;


    ///---------- Interest Portion Of The Installment ----------
    @NotNull(message = "Interest amount is required")
    @DecimalMin(value = "0.00", message = "Interest amount cannot be negative")
    @Column(name = "interest_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal interestAmount = BigDecimal.ZERO;


    ///---------- Total Amount Scheduled For This Installment ----------
    @NotNull(message = "Total installment amount is required")
    @DecimalMin(value = "0.01", message = "Total installment amount must be greater than zero")
    @Column(name = "total_installment_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal totalInstallmentAmount;


    ///---------- Amount Already Paid Against This Installment ----------
    @NotNull(message = "Paid amount is required")
    @DecimalMin(value = "0.00", message = "Paid amount cannot be negative")
    @Column(name = "paid_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;


    ///---------- Outstanding Amount Still Payable ----------
    @NotNull(message = "Remaining amount is required")
    @DecimalMin(value = "0.00", message = "Remaining amount cannot be negative")
    @Column(name = "remaining_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal remainingAmount = BigDecimal.ZERO;


    ///---------- Current LoanRepayment State Of The Installment ----------
    public enum InstallmentStatus {
        PENDING,
        PARTIAL,
        PAID,
        OVERDUE,
        CANCELLED
    }

    @NotNull(message = "Installment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "installment_status", nullable = false, length = 20)
    private InstallmentStatus installmentStatus = InstallmentStatus.PENDING;


    ///---------- Date When The Installment Became Fully Paid ----------
    @Column(name = "payment_date")
    private LocalDate paymentDate;


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