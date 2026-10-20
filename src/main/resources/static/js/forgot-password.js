/* ==========================================================
   FundAxis - Forgot Password page interactions (plain JavaScript)
   Guidance page only: no password reset, OTP, email or credential handling.
   ========================================================== */
(function () {
    'use strict';

    document.documentElement.classList.add('js-enabled');

    /* Move keyboard / screen-reader focus to the page heading on load */
    var heading = document.getElementById('recoveryHeading');
    if (heading) {
        heading.setAttribute('tabindex', '-1');
        heading.focus({ preventScroll: true });
    }
})();