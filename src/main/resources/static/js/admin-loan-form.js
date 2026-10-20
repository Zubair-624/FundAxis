/* FundAxis Admin Add Loan: trims the Employee ID / Loan Application ID and shows a saving state to prevent double submits. */
(function () {
    'use strict';
    var form = document.getElementById('loanForm');
    if (!form) { return; }
    var button = document.getElementById('saveLoanButton');
    var label = document.getElementById('saveLoanButtonText');
    var original = label ? label.textContent : '';

    form.addEventListener('submit', function () {
        ['employeeId', 'loanApplicationId'].forEach(function (id) {
            var el = document.getElementById(id);
            if (el) { el.value = el.value.trim(); }
        });
        if (form.checkValidity() && button) {
            button.disabled = true;
            if (label) { label.textContent = 'Saving...'; }
        }
    });

    // Restore the button when returning via the browser back button.
    window.addEventListener('pageshow', function () {
        if (button) { button.disabled = false; }
        if (label) { label.textContent = original; }
    });
})();