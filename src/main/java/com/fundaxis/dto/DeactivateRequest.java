package com.fundaxis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeactivateRequest {

    // Reason is required when an employee is deactivated
    @NotBlank(message = "Deactivation reason is required")
    private String deactivateReason;

}