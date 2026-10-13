// =========================================================
// FUNDAXIS LOGIN PAGE
//
// Responsibilities:
// 1. Password show/hide
// 2. Submit button loading state
// 3. Remove validation styling when user types
//
// IMPORTANT:
// Authentication and authorization are NOT handled here.
// Spring Security handles authentication server-side.
// =========================================================

// Wait until the HTML is fully loaded before touching any element
document.addEventListener("DOMContentLoaded", function () {
    initializePasswordVisibility();
    initializeLoginForm();
    initializeInputValidationReset();
});

// =========================================================
// PASSWORD VISIBILITY
// =========================================================

function initializePasswordVisibility() {
    // Grab the password field, the toggle button and both eye icons
    const passwordInput = document.getElementById("password");
    const passwordToggle = document.getElementById("passwordToggle");
    const eyeOpenIcon = document.getElementById("eyeOpenIcon");
    const eyeClosedIcon = document.getElementById("eyeClosedIcon");

    // Stop if the required elements are missing from the page
    if (!passwordInput || !passwordToggle) {
        return;
    }

    passwordToggle.addEventListener("click", function () {
        // True when the password is currently masked
        const passwordIsHidden = passwordInput.type === "password";

        // -----------------------------------------
        // Change input type
        // -----------------------------------------

        // Show the password as text, or mask it again
        passwordInput.type = passwordIsHidden ? "text" : "password";

        // -----------------------------------------
        // Accessibility
        // -----------------------------------------

        // Update the label so screen readers announce the next action
        passwordToggle.setAttribute(
            "aria-label",
            passwordIsHidden ? "Hide password" : "Show password"
        );

        // Tell assistive technology whether the toggle is active
        passwordToggle.setAttribute(
            "aria-pressed",
            passwordIsHidden ? "true" : "false"
        );

        // -----------------------------------------
        // Change icon
        // -----------------------------------------

        // Swap icons only if both exist; "d-none" hides an element
        if (eyeOpenIcon && eyeClosedIcon) {
            if (passwordIsHidden) {
                eyeOpenIcon.classList.add("d-none");
                eyeClosedIcon.classList.remove("d-none");
            } else {
                eyeOpenIcon.classList.remove("d-none");
                eyeClosedIcon.classList.add("d-none");
            }
        }

        // Keep keyboard focus on button
        passwordToggle.focus();
    });
}

// =========================================================
// LOGIN FORM
// =========================================================

function initializeLoginForm() {
    // Grab the form, its inputs and the submit button elements
    const loginForm = document.getElementById("loginForm");
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");
    const loginButton = document.getElementById("loginButton");
    const loginButtonText = document.getElementById("loginButtonText");

    // Stop if the form is not on this page
    if (!loginForm) {
        return;
    }

    loginForm.addEventListener("submit", function (event) {
        // Assume valid until a check below fails
        let formIsValid = true;

        // -----------------------------------------
        // Username validation
        // -----------------------------------------

        // Mark the username invalid if it is empty or only spaces
        if (usernameInput && usernameInput.value.trim() === "") {
            usernameInput.classList.add("invalid");
            formIsValid = false;
        }

        // -----------------------------------------
        // Password validation
        // -----------------------------------------

        // Mark the password invalid if it is empty
        if (passwordInput && passwordInput.value === "") {
            passwordInput.classList.add("invalid");
            formIsValid = false;
        }

        // -----------------------------------------
        // Stop only if browser-side required
        // fields are empty.
        //
        // We are NOT checking credentials here.
        // -----------------------------------------

        if (!formIsValid) {
            // Block the form submission
            event.preventDefault();

            // Move focus to the first field that failed validation
            const firstInvalidInput = loginForm.querySelector(
                ".login-input.invalid"
            );

            if (firstInvalidInput) {
                firstInvalidInput.focus();
            }

            return;
        }

        // -----------------------------------------
        // Loading UI
        //
        // This does NOT authenticate the user.
        // The form continues to Spring Security.
        // -----------------------------------------

        // Show the loading style and disable the button to prevent double submit
        if (loginButton) {
            loginButton.classList.add("loading");
            loginButton.disabled = true;
        }

        // Change the button text while the request is in progress
        if (loginButtonText) {
            loginButtonText.textContent = "Signing In...";
        }
    });
}

// =========================================================
// CLEAR VALIDATION WHEN USER STARTS TYPING
// =========================================================

function initializeInputValidationReset() {
    // Select every login input field
    const inputs = document.querySelectorAll(".login-input");

    inputs.forEach(function (input) {
        // Remove the error style as soon as the user types again
        input.addEventListener("input", function () {
            input.classList.remove("invalid");
        });
    });
}

// =========================================================
// SECURITY NOTE
//
// Do NOT add:
//
// localStorage.setItem("password", ...)
// sessionStorage password storage
// manual Basic Auth headers
// frontend role checking
// frontend authorization
//
// Credentials are submitted normally through:
// POST /login
//
// Spring Security must authenticate them.
// =========================================================