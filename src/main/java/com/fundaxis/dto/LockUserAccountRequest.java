package com.fundaxis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LockUserAccountRequest {

    // Date and time until which the user account should remain locked
    // Example: 07/10/2026 16:30:00
    // The time must be in the future.
    @NotNull(message = "Lock expiration time is required")
    @Future(message = "Lock expiration time must be in the future")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime lockedUntil;

}