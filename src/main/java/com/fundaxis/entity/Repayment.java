package com.fundaxis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(
        name = "repayments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_repayment_reference_number",
                        columnNames = "reference_number"
                )
        }
)
public class Repayment {


    ///---------- Database Primary Key ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    ///---------- Many Repayment Transactions Can Belong To One Repayment Schedule ----------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "repayment_schedule_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_repayment_schedule"
            )
    )
    private RepaymentSchedule repaymentSchedule;


    ///---------- Unique Business Reference For The Repayment Transaction ----------
    @NotBlank(message = "Reference number is required")
    @Size(
            max = 100,
            message = "Reference number cannot exceed 100 characters"
    )
    @Column(
            name = "reference_number",
            nullable = false,
            unique = true,
            length = 100
    )
    private String referenceNumber;


    ///---------- Date On Which The Repayment Was Received ----------
    @NotNull(message = "Repayment date is required")
    @Column(
            name = "repayment_date",
            nullable = false
    )
    private LocalDate repaymentDate;


    ///---------- Actual Amount Paid Against The Installment ----------
    @NotNull(message = "Repayment amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Repayment amount must be greater than zero"
    )
    @Column(
            name = "repayment_amount",
            nullable = false,
            precision = 20,
            scale = 2
    )
    private BigDecimal repaymentAmount;


    ///---------- Supported Repayment Payment Methods ----------
    public enum PaymentMethod {
        BANK_TRANSFER,
        CASH,
        CHEQUE,
        SALARY_DEDUCTION,
        OTHER
    }


    ///---------- Method Used To Receive The Repayment ----------
    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private PaymentMethod paymentMethod;


    ///---------- Repayment Transaction Status ----------
    public enum RepaymentStatus {
        COMPLETED,
        REVERSED
    }


    ///---------- Current State Of The Repayment Transaction ----------
    @NotNull(message = "Repayment status is required")
    @Enumerated(EnumType.STRING)
    @Column(
            name = "repayment_status",
            nullable = false,
            length = 20
    )
    private RepaymentStatus repaymentStatus =
            RepaymentStatus.COMPLETED;


    ///---------- Username Of The Administrator Who Recorded The Repayment ----------
    @Size(
            max = 100,
            message = "Recorded by cannot exceed 100 characters"
    )
    @Column(
            name = "recorded_by",
            length = 100
    )
    private String recordedBy;


    ///---------- Optional Remarks About The Repayment Transaction ----------
    @Size(
            max = 1000,
            message = "Remarks cannot exceed 1000 characters"
    )
    @Column(
            name = "remarks",
            length = 1000
    )
    private String remarks;


    ///---------- Reason For Reversing A Previously Completed Repayment ----------
    @Size(
            max = 1000,
            message = "Reversal reason cannot exceed 1000 characters"
    )
    @Column(
            name = "reversal_reason",
            length = 1000
    )
    private String reversalReason;


    ///---------- Date On Which The Repayment Was Reversed ----------
    @Column(
            name = "reversal_date"
    )
    private LocalDate reversalDate;


    ///---------- Administrator Who Reversed The Repayment ----------
    @Size(
            max = 100,
            message = "Reversed by cannot exceed 100 characters"
    )
    @Column(
            name = "reversed_by",
            length = 100
    )
    private String reversedBy;


    ///---------- Record Creation Timestamp ----------
    @CreationTimestamp
    @Column(
            name = "created_at",
            updatable = false
    )
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;


    ///---------- Record Last Update Timestamp ----------
    @UpdateTimestamp
    @Column(
            name = "updated_at"
    )
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
}