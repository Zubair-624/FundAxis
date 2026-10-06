package com.fundaxis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "loan_extensions")
public class LoanExtension {

    ///---------- Database Primary Key ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    ///---------- Many Extension Requests Can Belong To One Loan ----------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_extension_loan"))
    private Loan loan;


    ///---------- Loan End Date Before The Requested Extension ----------
    @NotNull(message = "Original end date is required")
    @Column(name = "original_end_date", nullable = false)
    private LocalDate originalEndDate;


    ///---------- New Loan End Date Requested By The Employee ----------
    @NotNull(message = "Requested end date is required")
    @Column(name = "requested_end_date", nullable = false)
    private LocalDate requestedEndDate;


    ///---------- Number Of Additional Months Requested ----------
    @NotNull(message = "Extension period is required")
    @Min(value = 1, message = "Extension period must be at least 1 month")
    @Column(name = "extension_months", nullable = false)
    private Integer extensionMonths;


    ///---------- Reason Provided For Requesting The Extension ----------
    @Column(name = "extension_reason", length = 1000)
    private String extensionReason;


    ///---------- Date When The Extension Request Was Submitted ----------
    @NotNull(message = "Request date is required")
    @PastOrPresent(message = "Request date cannot be in the future")
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;


    ///---------- Current Extension Request Status ----------
    public enum ExtensionStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }

    @NotNull(message = "Extension status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "extension_status", nullable = false, length = 20)
    private ExtensionStatus extensionStatus = ExtensionStatus.PENDING;


    ///---------- Null Until The Extension Is Approved ----------
    @Column(name = "approval_date")
    private LocalDate approvalDate;


    ///---------- Admin Or Authorized Person Who Approved The Extension ----------
    @Column(name = "approved_by", length = 100)
    private String approvedBy;


    ///---------- Null Unless The Extension Request Is Rejected ----------
    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;


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