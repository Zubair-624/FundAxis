package com.fundaxis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

///==================== Reset Password Request ====================///
// Request body sent by ADMIN / IT_SUPPORT to set a temporary password for another user
@Getter
@Setter
public class ResetPasswordRequest {

    ///---------- Temporary Password ----------
    // The temporary password given to the user, who must change it after the next login
    @NotBlank(message = "Temporary password is required")
    @Size(min = 10, max = 100, message = "Temporary password must be between 10 and 100 characters")
    private String temporaryPassword;


    ///---------- Confirm Password ----------
    // Must be exactly the same as the temporary password (this is checked in the service)
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}