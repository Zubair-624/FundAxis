/* ==========================================================
   FundAxis - Admin Add Contribution form (UI behavior only)
   - Turns the month picker value (yyyy-MM) into yyyy-MM-01 for the hidden
     "contributionMonth" field, so it binds to a date on the backend.
   - Trims the Employee ID and shows a loading state on submit.
   No authentication, authorization, status or business-rule logic:
   the backend (ContributionService) stays authoritative.
   ========================================================== */
(function () {
    'use strict';

    var form = document.getElementById('contributionForm');
    var monthInput = document.getElementById('contributionMonthInput');
    var monthHidden = document.getElementById('contributionMonth');
    var employeeId = document.getElementById('employeeId');
    var submitBtn = document.getElementById('saveContributionButton');
    var submitText = document.getElementById('saveContributionButtonText');

    if (!form || !monthInput || !monthHidden) {
        return;
    }

    var MONTH_PATTERN = /^\d{4}-(0[1-9]|1[0-2])$/;
    var defaultText = submitText ? submitText.textContent : '';

    /* First day of the selected month, or empty when the value is not a valid yyyy-MM */
    function syncMonth() {
        var value = monthInput.value.trim();
        monthHidden.value = MONTH_PATTERN.test(value) ? value + '-01' : '';
    }

    monthInput.addEventListener('input', syncMonth);
    monthInput.addEventListener('change', syncMonth);
    syncMonth();

    form.addEventListener('submit', function () {
        syncMonth();

        if (employeeId) {
            employeeId.value = employeeId.value.trim();
        }

        /* Native validation has already passed when submit fires */
        if (submitBtn) {
            submitBtn.disabled = true;
        }
        if (submitText) {
            submitText.textContent = 'Saving...';
        }
    });

    /* Restore the button when the page comes back from the browser cache (Back button) */
    window.addEventListener('pageshow', function (event) {
        if (event.persisted) {
            if (submitBtn) {
                submitBtn.disabled = false;
            }
            if (submitText) {
                submitText.textContent = defaultText;
            }
            syncMonth();
        }
    });
})();