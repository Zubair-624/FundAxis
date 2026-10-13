package com.fundaxis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserAccountRequest {

    // Username used by the employee to log in to FundAxis
    // Example: EMP001, john.smith, support01
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;


    // Raw password received only during account creation
    // IMPORTANT:
    // This value must never be stored directly in the database.
    // UserAccountService will encode/hash it before saving.
    @NotBlank(message = "Password is required")
    @Size(min = 10, max = 100, message = "Password must be between 10 and 100 characters")
    private String password;

}