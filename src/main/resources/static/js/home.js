/* ==========================================================
   FundAxis - Home page interactions (plain JavaScript)
   UI only: no authentication, authorization or business logic.
   ========================================================== */
(function () {
    'use strict';

    var navbar = document.querySelector('.fa-navbar');
    var navCollapse = document.getElementById('mainNav');
    var navLinks = document.querySelectorAll('.fa-navbar .nav-link');
    var scrollTopBtn = document.getElementById('scrollTopBtn');

    /* 1. Close the mobile menu after a navigation link is clicked */
    navLinks.forEach(function (link) {
        link.addEventListener('click', function () {
            if (navCollapse && navCollapse.classList.contains('show') && window.bootstrap) {
                window.bootstrap.Collapse.getOrCreateInstance(navCollapse).hide();
            }
        });
    });

    /* 2. Active navigation state based on the section in view */
    var sectionIds = ['home', 'about', 'features', 'how-it-works', 'contact'];
    var sections = sectionIds
        .map(function (id) { return document.getElementById(id); })
        .filter(Boolean);

    function setActive(id) {
        navLinks.forEach(function (link) {
            link.classList.toggle('active', link.getAttribute('href') === '#' + id);
        });
    }

    function updateActiveSection() {
        var offset = (navbar ? navbar.offsetHeight : 76) + 40;
        var current = sections.length ? sections[0].id : null;

        sections.forEach(function (section) {
            if (section.getBoundingClientRect().top - offset <= 0) {
                current = section.id;
            }
        });

        /* Bottom of the page always highlights the last item (Contact) */
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 4) {
            current = 'contact';
        }
        if (current) { setActive(current); }
    }

    /* 3. Navbar shadow + scroll-to-top button visibility */
    function onScroll() {
        if (navbar) { navbar.classList.toggle('is-scrolled', window.scrollY > 10); }
        if (scrollTopBtn) { scrollTopBtn.classList.toggle('show', window.scrollY > 400); }
        updateActiveSection();
    }

    if (scrollTopBtn) {
        scrollTopBtn.addEventListener('click', function () {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    }

    var ticking = false;
    window.addEventListener('scroll', function () {
        if (!ticking) {
            window.requestAnimationFrame(function () { onScroll(); ticking = false; });
            ticking = true;
        }
    }, { passive: true });

    window.addEventListener('resize', updateActiveSection);
    onScroll();
})();