package com.fundaxis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanDisbursementCancellationRequest {

    // Reason why the disbursement was cancelled
    @NotBlank(message = "Cancellation reason is required")
    private String cancellationReason;
}