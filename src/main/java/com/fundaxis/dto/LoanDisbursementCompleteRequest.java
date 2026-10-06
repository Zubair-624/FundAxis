package com.fundaxis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanDisbursementCompleteRequest {

    // Employee/admin who completed the disbursement
    @NotBlank(message = "Processed by is required")
    private String processedBy;


    // Unique reference for the completed transaction
    @NotBlank(message = "Reference number is required")
    private String referenceNumber;

}