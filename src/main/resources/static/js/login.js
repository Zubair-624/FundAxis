/* ==========================================================
   FundAxis - Login page interactions (plain JavaScript)
   UI only: no authentication, authorization or credential storage.
   The form is submitted normally to Spring Security (POST /login).
   ========================================================== */
(function () {
    'use strict';

    var form = document.getElementById('loginForm');
    var username = document.getElementById('username');
    var password = document.getElementById('password');
    var toggleBtn = document.getElementById('togglePassword');
    var submitBtn = document.getElementById('loginSubmit');

    /* 1. Show / hide password */
    if (toggleBtn && password) {
        toggleBtn.addEventListener('click', function () {
            var show = password.type === 'password';
            var icon = toggleBtn.querySelector('i');

            password.type = show ? 'text' : 'password';
            toggleBtn.setAttribute('aria-pressed', String(show));
            toggleBtn.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
            if (icon) {
                icon.classList.toggle('bi-eye', !show);
                icon.classList.toggle('bi-eye-slash', show);
            }
            password.focus();
        });
    }

    /* 2. Clear input error styling while typing */
    [username, password].forEach(function (input) {
        if (!input) { return; }
        input.addEventListener('input', function () {
            input.classList.remove('is-invalid');
        });
    });

    /* 3. Submit loading state (the browser still submits the form normally) */
    function resetSubmit() {
        if (!submitBtn) { return; }
        submitBtn.disabled = false;
        submitBtn.classList.remove('is-loading');
    }

    if (form && submitBtn) {
        form.addEventListener('submit', function () {
            submitBtn.classList.add('is-loading');
            submitBtn.disabled = true;
        });
        /* Restore the button if the user returns with the browser back button */
        window.addEventListener('pageshow', resetSubmit);
    }
})();