// =========================================================
// FUNDAXIS CHANGE PASSWORD PAGE
//
// Responsibilities:
// 1. Password show/hide for each password field
// 2. Minimum length indicator (live)
// 3. Password match indicator (live)
// 4. Client-side checks before submit (convenience only)
// 5. Submit button loading state
//
// IMPORTANT:
// Password changes are NOT handled here.
// The form is posted normally to POST /change-password and the
// backend validates and updates the password (server is authoritative).
// The backend also decides where the user goes next (/dashboard).
// =========================================================

// Minimum password length enforced by the backend
const MINIMUM_PASSWORD_LENGTH = 10;

// Wait until the HTML is fully loaded before touching any element
document.addEventListener("DOMContentLoaded", function () {
    initializePasswordVisibility();
    initializePasswordIndicators();
    initializeChangePasswordForm();
});

// =========================================================
// PASSWORD VISIBILITY
// =========================================================

function initializePasswordVisibility() {
    // Every eye button points to its input with data-toggle-target
    const toggleButtons = document.querySelectorAll("[data-toggle-target]");

    toggleButtons.forEach(function (toggleButton) {
        const passwordInput = document.getElementById(
            toggleButton.getAttribute("data-toggle-target")
        );
        const eyeOpenIcon = toggleButton.querySelector(".cp-eye-open");
        const eyeClosedIcon = toggleButton.querySelector(".cp-eye-closed");

        // Skip this button if its input is missing
        if (!passwordInput) {
            return;
        }

        toggleButton.addEventListener("click", function () {
            // True when the password is currently masked
            const passwordIsHidden = passwordInput.type === "password";

            // Show the password as text, or mask it again
            passwordInput.type = passwordIsHidden ? "text" : "password";

            // Accessibility: the label announces the next action
            toggleButton.setAttribute(
                "aria-label",
                passwordIsHidden ? "Hide password" : "Show password"
            );

            // Accessibility: tell assistive technology whether the toggle is active
            toggleButton.setAttribute(
                "aria-pressed",
                passwordIsHidden ? "true" : "false"
            );

            // Swap icons only if both exist; "d-none" hides an element
            if (eyeOpenIcon && eyeClosedIcon) {
                eyeOpenIcon.classList.toggle("d-none", passwordIsHidden);
                eyeClosedIcon.classList.toggle("d-none", !passwordIsHidden);
            }

            // Keep keyboard focus on the button
            toggleButton.focus();
        });
    });
}

// =========================================================
// LIVE INDICATORS (minimum length + password match)
// =========================================================

function initializePasswordIndicators() {
    const newPasswordInput = document.getElementById("newPassword");
    const confirmPasswordInput = document.getElementById("confirmPassword");

    // Stop if the page does not have the expected fields
    if (!newPasswordInput || !confirmPasswordInput) {
        return;
    }

    // Update both indicators whenever either field changes
    newPasswordInput.addEventListener("input", updateIndicators);
    confirmPasswordInput.addEventListener("input", updateIndicators);

    // Run once so the indicators start in the correct state
    updateIndicators();
}

function updateIndicators() {
    const newPasswordInput = document.getElementById("newPassword");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const lengthRule = document.getElementById("lengthRule");
    const matchHint = document.getElementById("matchHint");

    // Minimum length indicator: turns green once the minimum is reached
    if (lengthRule && newPasswordInput) {
        lengthRule.classList.toggle(
            "met",
            newPasswordInput.value.length >= MINIMUM_PASSWORD_LENGTH
        );
    }

    // Match indicator: only shown after the user starts typing the confirmation
    if (matchHint && newPasswordInput && confirmPasswordInput) {
        matchHint.classList.remove("match", "mismatch");

        if (confirmPasswordInput.value === "") {
            matchHint.textContent = "";
        } else if (confirmPasswordInput.value === newPasswordInput.value) {
            matchHint.textContent = "Passwords match.";
            matchHint.classList.add("match");
        } else {
            matchHint.textContent = "Passwords do not match.";
            matchHint.classList.add("mismatch");
        }
    }
}

// =========================================================
// FORM VALIDATION AND LOADING STATE
// =========================================================

function initializeChangePasswordForm() {
    const changePasswordForm = document.getElementById("changePasswordForm");
    const currentPasswordInput = document.getElementById("currentPassword");
    const newPasswordInput = document.getElementById("newPassword");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const changePasswordButton = document.getElementById("changePasswordButton");
    const changePasswordButtonText = document.getElementById("changePasswordButtonText");

    // Stop if the form is not on this page
    if (!changePasswordForm) {
        return;
    }

    // Remember the original button text so it can be restored later
    const defaultButtonText = changePasswordButtonText
        ? changePasswordButtonText.textContent
        : "";

    changePasswordForm.addEventListener("submit", function (event) {
        let formIsValid = true;

        // Start from a clean state on every submit attempt
        [currentPasswordInput, newPasswordInput, confirmPasswordInput].forEach(clearFieldError);

        // -----------------------------------------
        // Current password: required
        // -----------------------------------------

        if (currentPasswordInput && currentPasswordInput.value === "") {
            showFieldError(currentPasswordInput, "Enter your current password.");
            formIsValid = false;
        }

        // -----------------------------------------
        // New password: required and minimum length
        // -----------------------------------------

        if (newPasswordInput) {
            if (newPasswordInput.value === "") {
                showFieldError(newPasswordInput, "Enter a new password.");
                formIsValid = false;
            } else if (newPasswordInput.value.length < MINIMUM_PASSWORD_LENGTH) {
                showFieldError(
                    newPasswordInput,
                    "New password must be at least " + MINIMUM_PASSWORD_LENGTH + " characters."
                );
                formIsValid = false;
            }
        }

        // -----------------------------------------
        // Confirmation: required and must match
        // -----------------------------------------

        if (confirmPasswordInput) {
            if (confirmPasswordInput.value === "") {
                showFieldError(confirmPasswordInput, "Confirm your new password.");
                formIsValid = false;
            } else if (
                newPasswordInput &&
                confirmPasswordInput.value !== newPasswordInput.value
            ) {
                showFieldError(
                    confirmPasswordInput,
                    "New password and confirmation do not match."
                );
                formIsValid = false;
            }
        }

        // -----------------------------------------
        // Stop only when these browser-side checks fail.
        //
        // The backend still validates everything, including
        // whether the current password is correct.
        // -----------------------------------------

        if (!formIsValid) {
            event.preventDefault();

            // Move focus to the first field with an error
            const firstInvalidInput = changePasswordForm.querySelector(".cp-input.invalid");

            if (firstInvalidInput) {
                firstInvalidInput.focus();
            }

            return;
        }

        // -----------------------------------------
        // Loading UI
        //
        // This does NOT change the password.
        // The form continues to POST /change-password.
        // -----------------------------------------

        if (changePasswordButton) {
            changePasswordButton.classList.add("loading");
            changePasswordButton.disabled = true;
        }

        if (changePasswordButtonText) {
            changePasswordButtonText.textContent = "Changing Password...";
        }
    });

    // Clear a field's error as soon as the user edits it
    [currentPasswordInput, newPasswordInput, confirmPasswordInput].forEach(function (input) {
        if (input) {
            input.addEventListener("input", function () {
                clearFieldError(input);
            });
        }
    });

    // If the user comes back with the browser Back button, the page may be
    // restored from cache with the button still disabled, so reset it
    window.addEventListener("pageshow", function (event) {
        if (event.persisted && changePasswordButton) {
            changePasswordButton.classList.remove("loading");
            changePasswordButton.disabled = false;

            if (changePasswordButtonText) {
                changePasswordButtonText.textContent = defaultButtonText;
            }
        }
    });
}

// =========================================================
// FIELD ERROR HELPERS
// =========================================================

// Show an error message under a field (the message element id is <fieldId>Error)
function showFieldError(input, message) {
    const errorElement = document.getElementById(input.id + "Error");

    input.classList.add("invalid");
    input.setAttribute("aria-invalid", "true");

    if (errorElement) {
        errorElement.textContent = message;
        errorElement.hidden = false;
    }
}

// Remove the error state and message from a field
function clearFieldError(input) {
    if (!input) {
        return;
    }

    const errorElement = document.getElementById(input.id + "Error");

    input.classList.remove("invalid");
    input.removeAttribute("aria-invalid");

    if (errorElement) {
        errorElement.textContent = "";
        errorElement.hidden = true;
    }
}

// =========================================================
// SECURITY NOTE
//
// Do NOT add:
//
// localStorage.setItem("password", ...)
// sessionStorage password storage
// frontend role checking
// frontend authorization
// hardcoded dashboard redirects
//
// Passwords are submitted normally through:
// POST /change-password
//
// The backend must validate and update them.
// =========================================================