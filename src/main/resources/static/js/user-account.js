document.addEventListener("DOMContentLoaded", function () {


    // ============================================================
    // 1. PASSWORD SHOW / HIDE
    // ============================================================

    document.querySelectorAll("[data-password-target]")
        .forEach(function (button) {

            button.addEventListener("click", function () {

                const targetId =
                    button.getAttribute("data-password-target");

                const input =
                    document.getElementById(targetId);

                if (!input) {
                    return;
                }


                const icon =
                    button.querySelector("i");


                const showingPassword =
                    input.type === "text";


                input.type =
                    showingPassword
                        ? "password"
                        : "text";


                button.setAttribute(
                    "aria-pressed",
                    showingPassword
                        ? "false"
                        : "true"
                );


                button.setAttribute(
                    "aria-label",
                    showingPassword
                        ? "Show password"
                        : "Hide password"
                );


                if (icon) {

                    icon.classList.toggle(
                        "bi-eye",
                        showingPassword
                    );

                    icon.classList.toggle(
                        "bi-eye-slash",
                        !showingPassword
                    );
                }
            });
        });


    // ============================================================
    // 2. PASSWORD CONFIRMATION MATCH
    // ============================================================

    document.querySelectorAll("[data-password-form]")
        .forEach(function (form) {

            const newPassword =
                form.querySelector("[data-pw-new]");

            const confirmPassword =
                form.querySelector("[data-pw-confirm]");

            const hint =
                form.querySelector("[data-pw-hint]");


            if (!newPassword
                || !confirmPassword) {

                return;
            }


            function validatePasswordMatch() {

                const firstValue =
                    newPassword.value;

                const secondValue =
                    confirmPassword.value;


                if (!secondValue) {

                    confirmPassword.setCustomValidity("");

                    if (hint) {

                        hint.textContent = "";

                        hint.classList.remove(
                            "text-success",
                            "text-danger"
                        );
                    }

                    return;
                }


                if (firstValue === secondValue) {

                    confirmPassword.setCustomValidity("");

                    if (hint) {

                        hint.textContent =
                            "Passwords match.";

                        hint.classList.remove(
                            "text-danger"
                        );

                        hint.classList.add(
                            "text-success"
                        );
                    }

                } else {

                    confirmPassword.setCustomValidity(
                        "Passwords do not match."
                    );


                    if (hint) {

                        hint.textContent =
                            "Passwords do not match.";

                        hint.classList.remove(
                            "text-success"
                        );

                        hint.classList.add(
                            "text-danger"
                        );
                    }
                }
            }


            newPassword.addEventListener(
                "input",
                validatePasswordMatch
            );


            confirmPassword.addEventListener(
                "input",
                validatePasswordMatch
            );


            form.addEventListener(
                "submit",
                validatePasswordMatch
            );
        });


    // ============================================================
    // 3. SHARED CONFIRMATION MODAL
    // ============================================================

    const confirmModal =
        document.getElementById("confirmModal");


    if (confirmModal) {

        confirmModal.addEventListener(
            "show.bs.modal",
            function (event) {

                const triggerButton =
                    event.relatedTarget;


                if (!triggerButton) {
                    return;
                }


                const actionUrl =
                    triggerButton.getAttribute(
                        "data-action-url"
                    );


                const title =
                    triggerButton.getAttribute(
                        "data-title"
                    )
                    || "Confirm Action";


                const message =
                    triggerButton.getAttribute(
                        "data-message"
                    )
                    || "Are you sure?";


                const confirmLabel =
                    triggerButton.getAttribute(
                        "data-confirm-label"
                    )
                    || "Confirm";


                const confirmStyle =
                    triggerButton.getAttribute(
                        "data-confirm-style"
                    )
                    || "primary";


                const form =
                    document.getElementById(
                        "confirmForm"
                    );


                const titleElement =
                    document.getElementById(
                        "confirmTitle"
                    );


                const messageElement =
                    document.getElementById(
                        "confirmMessage"
                    );


                const submitButton =
                    document.getElementById(
                        "confirmSubmit"
                    );


                if (form
                    && actionUrl) {

                    form.action =
                        actionUrl;
                }


                if (titleElement) {

                    titleElement.textContent =
                        title;
                }


                if (messageElement) {

                    messageElement.textContent =
                        message;
                }


                if (submitButton) {

                    submitButton.textContent =
                        confirmLabel;


                    submitButton.classList.remove(
                        "fa-btn-primary",
                        "fa-btn-danger"
                    );


                    if (confirmStyle === "danger") {

                        submitButton.classList.add(
                            "fa-btn-danger"
                        );

                    } else {

                        submitButton.classList.add(
                            "fa-btn-primary"
                        );
                    }
                }
            }
        );
    }


    // ============================================================
    // 4. QUICK LOCK TIME BUTTONS
    // ============================================================

    document.querySelectorAll("[data-lock-minutes]")
        .forEach(function (button) {

            button.addEventListener(
                "click",
                function () {

                    const minutes =
                        parseInt(
                            button.getAttribute(
                                "data-lock-minutes"
                            ),
                            10
                        );


                    if (Number.isNaN(minutes)) {
                        return;
                    }


                    const input =
                        document.getElementById(
                            "lockedUntil"
                        );


                    if (!input) {
                        return;
                    }


                    const date =
                        new Date();


                    date.setMinutes(
                        date.getMinutes()
                        + minutes
                    );


                    input.value =
                        toLocalDateTimeValue(
                            date
                        );
                }
            );
        });


    // ============================================================
    // 5. PREVENT DOUBLE SUBMISSION
    // ============================================================

    document.querySelectorAll("[data-submit-once]")
        .forEach(function (form) {

            form.addEventListener(
                "submit",
                function (event) {

                    if (!form.checkValidity()) {
                        return;
                    }


                    if (form.dataset.submitted
                        === "true") {

                        event.preventDefault();

                        return;
                    }


                    form.dataset.submitted =
                        "true";


                    const submitButton =
                        form.querySelector(
                            'button[type="submit"]'
                        );


                    if (!submitButton) {
                        return;
                    }


                    const loadingText =
                        submitButton.getAttribute(
                            "data-loading-text"
                        );


                    submitButton.disabled =
                        true;


                    if (loadingText) {

                        const buttonText =
                            submitButton.querySelector(
                                "[data-btn-text]"
                            );


                        if (buttonText) {

                            buttonText.textContent =
                                loadingText;

                        } else {

                            submitButton.textContent =
                                loadingText;
                        }
                    }
                }
            );
        });


    // ============================================================
    // HELPER
    // ============================================================

    function toLocalDateTimeValue(date) {

        const pad =
            function (value) {

                return String(value)
                    .padStart(2, "0");
            };


        return date.getFullYear()
            + "-"
            + pad(date.getMonth() + 1)
            + "-"
            + pad(date.getDate())
            + "T"
            + pad(date.getHours())
            + ":"
            + pad(date.getMinutes());
    }

});