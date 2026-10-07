package com.fundaxis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
@Entity
@Table(name = "loan_repayments", uniqueConstraints = {@UniqueConstraint(name = "uk_loan_repayment_reference_number", columnNames = "reference_number")})
public class LoanRepayment {

    ///---------- Database Primary Key ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    ///---------- Repayment Schedule Installment Associated With This Payment ----------
    // One installment can have multiple repayment transactions
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repayment_schedule_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_repayment_repayment_schedule"))
    private RepaymentSchedule repaymentSchedule;


    ///---------- Actual Amount Paid By The Employee ----------
    @NotNull(message = "Repayment amount is required")
    @DecimalMin(value = "0.01", message = "Repayment amount must be greater than zero")
    @Column(name = "repayment_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal repaymentAmount;


    ///---------- Date When The Employee Made The Repayment ----------
    @NotNull(message = "Repayment date is required")
    @PastOrPresent(message = "Repayment date cannot be in the future")
    @Column(name = "repayment_date", nullable = false)
    private LocalDate repaymentDate;


    ///---------- Method Used To Make The Repayment ----------
    public enum PaymentMethod {
        BANK_TRANSFER,
        CASH,
        CHEQUE,
        OTHER
    }

    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;


    ///---------- Unique Reference Used To Trace The Repayment Transaction ----------
    @NotBlank(message = "Reference number is required")
    @Column(name = "reference_number", nullable = false, length = 100, unique = true)
    private String referenceNumber;


    ///---------- Current Processing Status Of The Repayment Transaction ----------
    public enum RepaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    @NotNull(message = "Repayment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "repayment_status", nullable = false, length = 20)
    private RepaymentStatus repaymentStatus = RepaymentStatus.PENDING;


    ///---------- Admin Or Authorized User Who Completed The Processing ----------
    @Column(name = "processed_by", length = 100)
    private String processedBy;


    ///---------- Date When Repayment Processing Started ----------
    @Column(name = "processing_date")
    private LocalDate processingDate;


    ///---------- Reason Recorded When Repayment Processing Fails ----------
    @Column(name = "failure_reason", length = 1000)
    private String failureReason;


    ///---------- Reason Recorded When Repayment Is Cancelled ----------
    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;


    ///---------- Additional Operational Information ----------
    @Column(name = "remarks", length = 1000)
    private String remarks;


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