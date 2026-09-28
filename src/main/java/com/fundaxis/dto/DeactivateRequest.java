package com.fundaxis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeactivateRequest {

    @NotBlank(message = "Deactivation reason is required")
    private String deactivateReason;
}