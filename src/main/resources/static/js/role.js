document.addEventListener("DOMContentLoaded", function () {


    // ============================================================
    // 1. ROLE CODE NORMALIZATION
    // ============================================================

    document.querySelectorAll("[data-role-code]")
        .forEach(function (input) {

            input.addEventListener("input", function () {

                const start =
                    input.selectionStart;

                const end =
                    input.selectionEnd;


                input.value =
                    input.value
                        .toUpperCase()
                        .replace(/[^A-Z0-9_]/g, "");


                try {

                    input.setSelectionRange(
                        start,
                        end
                    );

                } catch (error) {

                    // Ignore selection errors for unsupported browsers.
                }
            });
        });


    // ============================================================
    // 2. CHARACTER COUNTERS
    // ============================================================

    document.querySelectorAll("[data-char-counter]")
        .forEach(function (field) {

            const targetId =
                field.getAttribute(
                    "data-char-counter"
                );


            const counter =
                document.getElementById(
                    targetId
                );


            if (!counter) {
                return;
            }


            const maxLength =
                parseInt(
                    field.getAttribute(
                        "maxlength"
                    ),
                    10
                );


            function updateCounter() {

                const length =
                    field.value.length;


                if (Number.isNaN(maxLength)) {

                    counter.textContent =
                        length + " characters";

                    return;
                }


                counter.textContent =
                    length
                    + " / "
                    + maxLength;


                counter.classList.remove(
                    "role-char-counter-warning",
                    "role-char-counter-danger"
                );


                const warningThreshold =
                    Math.floor(
                        maxLength * 0.8
                    );


                const dangerThreshold =
                    Math.floor(
                        maxLength * 0.95
                    );


                if (length >= dangerThreshold) {

                    counter.classList.add(
                        "role-char-counter-danger"
                    );

                } else if (length >= warningThreshold) {

                    counter.classList.add(
                        "role-char-counter-warning"
                    );
                }
            }


            updateCounter();


            field.addEventListener(
                "input",
                updateCounter
            );
        });


    // ============================================================
    // 3. SHARED CONFIRMATION MODAL
    // ============================================================

    const confirmModal =
        document.getElementById(
            "roleConfirmModal"
        );


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
                    || "Are you sure you want to continue?";


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
                        "roleConfirmForm"
                    );


                const titleElement =
                    document.getElementById(
                        "roleConfirmTitle"
                    );


                const messageElement =
                    document.getElementById(
                        "roleConfirmMessage"
                    );


                const submitButton =
                    document.getElementById(
                        "roleConfirmSubmit"
                    );


                if (form && actionUrl) {

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
                        "fa-btn-danger",
                        "role-btn-warning-outline",
                        "role-btn-danger-outline"
                    );


                    if (confirmStyle === "danger") {

                        submitButton.classList.add(
                            "fa-btn-danger"
                        );

                    } else if (confirmStyle === "warning") {

                        submitButton.classList.add(
                            "role-btn-warning-outline"
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
    // 4. PREVENT DOUBLE FORM SUBMISSION
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
    // 5. SYSTEM ROLE CODE PROTECTION
    // ============================================================

    document.querySelectorAll("[data-system-role-code]")
        .forEach(function (input) {

            const originalValue =
                input.value;


            input.addEventListener(
                "input",
                function () {

                    input.value =
                        originalValue;
                }
            );


            input.addEventListener(
                "keydown",
                function (event) {

                    const allowedKeys = [
                        "Tab",
                        "Shift",
                        "Control",
                        "Alt",
                        "Meta",
                        "ArrowLeft",
                        "ArrowRight"
                    ];


                    if (!allowedKeys.includes(
                        event.key
                    )) {

                        event.preventDefault();
                    }
                }
            );
        });


    // ============================================================
    // 6. ROLE FILTER AUTO-SUBMIT
    // ============================================================

    document.querySelectorAll("[data-role-filter]")
        .forEach(function (select) {

            select.addEventListener(
                "change",
                function () {

                    const form =
                        select.closest("form");


                    if (form) {

                        form.submit();
                    }
                }
            );
        });


    // ============================================================
    // 7. SEARCH FIELD CLEAR BUTTON
    // ============================================================

    document.querySelectorAll("[data-clear-target]")
        .forEach(function (button) {

            button.addEventListener(
                "click",
                function () {

                    const targetId =
                        button.getAttribute(
                            "data-clear-target"
                        );


                    const input =
                        document.getElementById(
                            targetId
                        );


                    if (!input) {
                        return;
                    }


                    input.value = "";

                    input.focus();
                }
            );
        });


    // ============================================================
    // 8. ROLE FORM VALIDATION
    // ============================================================

    document.querySelectorAll("[data-role-form]")
        .forEach(function (form) {

            form.addEventListener(
                "submit",
                function (event) {

                    const roleCode =
                        form.querySelector(
                            '[name="roleCode"]'
                        );


                    const roleName =
                        form.querySelector(
                            '[name="roleName"]'
                        );


                    if (roleCode
                        && !roleCode.disabled
                        && !roleCode.readOnly) {

                        const normalizedCode =
                            roleCode.value
                                .trim()
                                .toUpperCase();


                        roleCode.value =
                            normalizedCode;


                        const rolePattern =
                            /^[A-Z][A-Z0-9_]*$/;


                        if (!rolePattern.test(
                            normalizedCode
                        )) {

                            roleCode.setCustomValidity(
                                "Role code must start with an uppercase letter and contain only uppercase letters, numbers, and underscores."
                            );

                        } else {

                            roleCode.setCustomValidity("");
                        }
                    }


                    if (roleName) {

                        roleName.value =
                            roleName.value.trim();


                        if (!roleName.value) {

                            roleName.setCustomValidity(
                                "Role name is required."
                            );

                        } else {

                            roleName.setCustomValidity("");
                        }
                    }


                    if (!form.checkValidity()) {

                        event.preventDefault();

                        event.stopPropagation();

                        form.reportValidity();
                    }
                }
            );
        });


    // ============================================================
    // 9. REMOVE CUSTOM VALIDITY WHILE TYPING
    // ============================================================

    document.querySelectorAll(
        '[name="roleCode"], [name="roleName"]'
    )
        .forEach(function (field) {

            field.addEventListener(
                "input",
                function () {

                    field.setCustomValidity("");
                }
            );
        });


    // ============================================================
    // 10. COPY ROLE CODE
    // ============================================================

    document.querySelectorAll("[data-copy-role-code]")
        .forEach(function (button) {

            button.addEventListener(
                "click",
                async function () {

                    const roleCode =
                        button.getAttribute(
                            "data-copy-role-code"
                        );


                    if (!roleCode) {
                        return;
                    }


                    try {

                        await navigator.clipboard
                            .writeText(roleCode);


                        const originalText =
                            button.innerHTML;


                        button.innerHTML =
                            '<i class="bi bi-check2"></i> Copied';


                        setTimeout(
                            function () {

                                button.innerHTML =
                                    originalText;
                            },
                            1500
                        );


                    } catch (error) {

                        // Clipboard API may be unavailable on some browsers.
                        console.warn(
                            "Unable to copy role code."
                        );
                    }
                }
            );
        });

});