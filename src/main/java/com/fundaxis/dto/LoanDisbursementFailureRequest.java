package com.fundaxis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanDisbursementFailureRequest {

    // Reason why the disbursement failed
    @NotBlank(message = "Failure reason is required")
    private String failureReason;
}