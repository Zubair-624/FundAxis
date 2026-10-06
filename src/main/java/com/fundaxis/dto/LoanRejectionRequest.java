package com.fundaxis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRejectionRequest {

    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;

}