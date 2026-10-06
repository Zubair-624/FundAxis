package com.fundaxis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "loan_disbursements", uniqueConstraints = {@UniqueConstraint(name = "uk_disbursement_reference_number", columnNames = "reference_number")})
public class LoanDisbursement {

    ///---------- Database Primary Key ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    ///---------- Many Disbursement Records Can Belong To One Loan ----------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_disbursement_loan"))
    private Loan loan;


    ///---------- Amount Of Money Included In This Disbursement ----------
    @NotNull(message = "Disbursement amount is required")
    @DecimalMin(value = "0.01", message = "Disbursement amount must be greater than zero")
    @Column(name = "disbursement_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal disbursementAmount;


    ///---------- Date When The Money Was Actually Disbursed ----------
    // Null until the disbursement is successfully completed
    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;


    ///---------- Current Stage Of The Disbursement ----------
    public enum DisbursementStatus {
        PENDING,
        PROCESSING,
        DISBURSED,
        FAILED,
        CANCELLED
    }

    @NotNull(message = "Disbursement status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "disbursement_status", nullable = false, length = 20)
    private DisbursementStatus disbursementStatus = DisbursementStatus.PENDING;


    ///---------- Method Used To Release The Loan Funds ----------
    public enum PaymentMethod {
        BANK_TRANSFER,
        CASH,
        CHEQUE,
        OTHER
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;


    ///---------- Unique Reference Used To Trace The Payment Transaction ----------
    // Example: TXN-2026-000125
    @Column(name = "reference_number", length = 100, unique = true)
    private String referenceNumber;


    ///---------- Employee/Admin Who Processed The Disbursement ----------
    // Kept as String until authentication/user relationships are implemented
    @Column(name = "processed_by", length = 100)
    private String processedBy;


    ///---------- Date When Processing Of The Disbursement Occurred ----------
    @Column(name = "processing_date")
    private LocalDate processingDate;


    ///---------- Reason Recorded When The Disbursement Fails ----------
    @Column(name = "failure_reason", length = 1000)
    private String failureReason;


    ///---------- Reason Recorded When The Disbursement Is Canceled ----------
    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;


    ///---------- General Operational Notes About The Disbursement ----------
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