package com.fundaxis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

///==================== Change Password Request ====================///
// Request body sent by a logged-in user to change their own password
@Getter
@Setter
public class ChangePasswordRequest {

    ///---------- Current Password ----------
    // The user's existing password, checked against the stored hash before any change
    @NotBlank(message = "Current password is required")
    private String currentPassword;


    ///---------- New Password ----------
    // The password the user wants to use from now on
    @NotBlank(message = "New password is required")
    @Size(min = 10, max = 100, message = "New password must be between 10 and 100 characters")
    private String newPassword;


    ///---------- Confirm Password ----------
    // Must be exactly the same as the new password (this is checked in the service)
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}