/* ==========================================================
   FundAxis — Common Dashboard (UI behavior only)
   No authentication, authorization, role, or credential logic.
   Spring Security on the backend remains authoritative.
   ========================================================== */
(function () {
    'use strict';

    var sidebar = document.getElementById('faSidebar');
    var overlay = document.getElementById('faOverlay');
    var toggleBtn = document.getElementById('faMenuToggle');
    var closeBtn = document.getElementById('faSidebarClose');
    var mobileQuery = window.matchMedia('(max-width: 991.98px)');

    if (!sidebar || !toggleBtn) {
        return;
    }

    function isMobile() {
        return mobileQuery.matches;
    }

    function openSidebar() {
        sidebar.classList.add('is-open');
        if (overlay) { overlay.hidden = false; }
        toggleBtn.setAttribute('aria-expanded', 'true');
        document.body.classList.add('fa-no-scroll');
        var firstLink = sidebar.querySelector('a, button');
        if (firstLink) { firstLink.focus(); }
    }

    function closeSidebar(returnFocus) {
        sidebar.classList.remove('is-open');
        if (overlay) { overlay.hidden = true; }
        toggleBtn.setAttribute('aria-expanded', 'false');
        document.body.classList.remove('fa-no-scroll');
        if (returnFocus) { toggleBtn.focus(); }
    }

    toggleBtn.addEventListener('click', function () {
        if (sidebar.classList.contains('is-open')) {
            closeSidebar(true);
        } else {
            openSidebar();
        }
    });

    if (closeBtn) {
        closeBtn.addEventListener('click', function () { closeSidebar(true); });
    }

    if (overlay) {
        overlay.addEventListener('click', function () { closeSidebar(true); });
    }

    // Close with Escape key
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape' && sidebar.classList.contains('is-open')) {
            closeSidebar(true);
        }
    });

    // Close sidebar after navigating (mobile only)
    sidebar.querySelectorAll('a.fa-nav-link').forEach(function (link) {
        link.addEventListener('click', function () {
            if (isMobile()) { closeSidebar(false); }
        });
    });

    // Reset state when moving to desktop width
    function handleBreakpointChange() {
        if (!isMobile()) { closeSidebar(false); }
    }
    if (mobileQuery.addEventListener) {
        mobileQuery.addEventListener('change', handleBreakpointChange);
    } else if (mobileQuery.addListener) {
        mobileQuery.addListener(handleBreakpointChange);
    }

    // Keep keyboard focus inside the sidebar while it is open on mobile
    sidebar.addEventListener('keydown', function (event) {
        if (event.key !== 'Tab' || !isMobile() || !sidebar.classList.contains('is-open')) { return; }
        var focusable = sidebar.querySelectorAll('a[href], button:not([disabled])');
        if (!focusable.length) { return; }
        var first = focusable[0];
        var last = focusable[focusable.length - 1];
        if (event.shiftKey && document.activeElement === first) {
            event.preventDefault();
            last.focus();
        } else if (!event.shiftKey && document.activeElement === last) {
            event.preventDefault();
            first.focus();
        }
    });

    // Visual-only active link helper: highlights the link matching the current URL.
    // Skipped if the server already marked an item active.
    (function markActiveLink() {
        var links = sidebar.querySelectorAll('a.fa-nav-link');
        var path = window.location.pathname.replace(/\/+$/, '') || '/';
        var best = null;
        links.forEach(function (link) {
            var linkPath = new URL(link.href, window.location.origin).pathname.replace(/\/+$/, '') || '/';
            if (linkPath === path || (linkPath !== '/' && path.indexOf(linkPath + '/') === 0)) {
                if (!best || linkPath.length > best.len) {
                    best = { el: link, len: linkPath.length };
                }
            }
        });
        if (best) {
            links.forEach(function (l) {
                l.classList.remove('active');
                l.removeAttribute('aria-current');
            });
            best.el.classList.add('active');
            best.el.setAttribute('aria-current', 'page');
        }
    })();
})();