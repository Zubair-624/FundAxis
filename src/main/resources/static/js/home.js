// =========================================================
// FUNDAXIS - HOME PAGE JAVASCRIPT
// =========================================================


// =========================================================
// DOM READY
// =========================================================

document.addEventListener("DOMContentLoaded", function () {

    // Initialize all home page functions
    handleNavbarScroll();
    handleBackToTop();
    handleCurrentYear();
    handleSmoothNavigation();
    handleActiveNavigation();
    handleMobileNavbar();
    handleRevealAnimation();

});


// =========================================================
// NAVBAR SCROLL EFFECT
// =========================================================

function handleNavbarScroll() {

    const navbar = document.querySelector(".fundaxis-navbar");

    if (!navbar) {
        return;
    }

    function updateNavbar() {

        if (window.scrollY > 40) {
            navbar.classList.add("navbar-scrolled");
        } else {
            navbar.classList.remove("navbar-scrolled");
        }

    }

    // Run once when page loads
    updateNavbar();

    // Run whenever user scrolls
    window.addEventListener("scroll", updateNavbar);

}


// =========================================================
// BACK TO TOP BUTTON
// =========================================================

function handleBackToTop() {

    const backToTopButton = document.getElementById("backToTop");

    if (!backToTopButton) {
        return;
    }

    function toggleBackToTopButton() {

        if (window.scrollY > 450) {
            backToTopButton.classList.add("show");
        } else {
            backToTopButton.classList.remove("show");
        }

    }

    // Check button visibility on page load
    toggleBackToTopButton();

    // Check button visibility on scroll
    window.addEventListener("scroll", toggleBackToTopButton);

    // Scroll smoothly to top
    backToTopButton.addEventListener("click", function () {

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    });

}


// =========================================================
// CURRENT YEAR
// =========================================================

function handleCurrentYear() {

    const currentYearElement = document.getElementById("currentYear");

    if (!currentYearElement) {
        return;
    }

    const currentYear = new Date().getFullYear();

    currentYearElement.textContent = currentYear;

}


// =========================================================
// SMOOTH NAVIGATION
// =========================================================

function handleSmoothNavigation() {

    const navigationLinks = document.querySelectorAll('a[href^="#"]');

    navigationLinks.forEach(function (link) {

        link.addEventListener("click", function (event) {

            const targetId = this.getAttribute("href");

            // Ignore empty placeholder links
            if (
                !targetId ||
                targetId === "#" ||
                targetId === "#!"
            ) {
                return;
            }

            const targetSection = document.querySelector(targetId);

            if (!targetSection) {
                return;
            }

            event.preventDefault();

            const navbar = document.querySelector(".fundaxis-navbar");

            let navbarHeight = 0;

            if (navbar) {
                navbarHeight = navbar.offsetHeight;
            }

            const sectionPosition =
                targetSection.getBoundingClientRect().top
                + window.scrollY
                - navbarHeight
                + 1;

            window.scrollTo({
                top: sectionPosition,
                behavior: "smooth"
            });

        });

    });

}


// =========================================================
// ACTIVE NAVIGATION LINK
// =========================================================

function handleActiveNavigation() {

    const sections = document.querySelectorAll("section[id], footer[id]");

    const navLinks = document.querySelectorAll(".fundaxis-navbar .nav-link");

    if (
        sections.length === 0 ||
        navLinks.length === 0
    ) {
        return;
    }

    function updateActiveNavigation() {

        let currentSection = "";

        const scrollPosition = window.scrollY + 160;

        sections.forEach(function (section) {

            const sectionTop = section.offsetTop;
            const sectionHeight = section.offsetHeight;

            if (
                scrollPosition >= sectionTop &&
                scrollPosition < sectionTop + sectionHeight
            ) {
                currentSection = section.getAttribute("id");
            }

        });

        navLinks.forEach(function (link) {

            link.classList.remove("active");

            const linkTarget = link.getAttribute("href");

            if (
                currentSection &&
                linkTarget === "#" + currentSection
            ) {
                link.classList.add("active");
            }

        });

    }

    // Run once on page load
    updateActiveNavigation();

    // Update active section on scroll
    window.addEventListener("scroll", updateActiveNavigation);

}


// =========================================================
// MOBILE NAVBAR
// =========================================================

function handleMobileNavbar() {

    const navbarCollapse = document.getElementById("fundAxisNavbar");

    const navLinks = document.querySelectorAll("#fundAxisNavbar .nav-link");

    if (!navbarCollapse) {
        return;
    }

    navLinks.forEach(function (link) {

        link.addEventListener("click", function () {

            // Only close menu on smaller screens
            if (window.innerWidth < 992) {

                const bootstrapCollapse =
                    bootstrap.Collapse.getOrCreateInstance(navbarCollapse);

                bootstrapCollapse.hide();

            }

        });

    });

}


// =========================================================
// SCROLL REVEAL ANIMATION
// =========================================================

function handleRevealAnimation() {

    /*
     * Elements that will animate when they become visible.
     *
     * The CSS already contains:
     *
     * .reveal
     * .reveal.active
     */

    const revealElements = document.querySelectorAll(
        `
        .section-heading,
        .about-visual,
        .about-content,
        .feature-card,
        .why-heading,
        .why-card,
        .process-card,
        .cta-box
        `
    );

    if (revealElements.length === 0) {
        return;
    }

    // Add reveal class automatically
    revealElements.forEach(function (element) {
        element.classList.add("reveal");
    });

    // Browser support check
    if (!("IntersectionObserver" in window)) {

        revealElements.forEach(function (element) {
            element.classList.add("active");
        });

        return;

    }

    const observerOptions = {
        root: null,
        rootMargin: "0px 0px -70px 0px",
        threshold: 0.12
    };

    const revealObserver = new IntersectionObserver(
        function (entries, observer) {

            entries.forEach(function (entry) {

                if (entry.isIntersecting) {

                    entry.target.classList.add("active");

                    // Animate only once
                    observer.unobserve(entry.target);

                }

            });

        },
        observerOptions
    );

    revealElements.forEach(function (element) {
        revealObserver.observe(element);
    });

}


// =========================================================
// DASHBOARD CARD HOVER INTERACTION
// =========================================================

const dashboardCards = document.querySelectorAll(".mini-stat-card");

dashboardCards.forEach(function (card) {

    card.addEventListener("mouseenter", function () {
        card.style.transform = "translateY(-4px)";
    });

    card.addEventListener("mouseleave", function () {
        card.style.transform = "";
    });

});


// =========================================================
// HERO BUTTON ARROW INTERACTION
// =========================================================

const primaryButtons = document.querySelectorAll(".hero-primary-btn");

primaryButtons.forEach(function (button) {

    button.addEventListener("mouseenter", function () {

        const arrow = button.querySelector(".bi-arrow-right");

        if (arrow) {
            arrow.style.transform = "translateX(5px)";
        }

    });

    button.addEventListener("mouseleave", function () {

        const arrow = button.querySelector(".bi-arrow-right");

        if (arrow) {
            arrow.style.transform = "";
        }

    });

});


// =========================================================
// FEATURE CARD INTERACTION
// =========================================================

const featureCards = document.querySelectorAll(".feature-card");

featureCards.forEach(function (card) {

    card.addEventListener("mouseenter", function () {

        const icon = card.querySelector(".feature-icon");

        if (icon) {
            icon.style.transform = "scale(1.05) rotate(-2deg)";
        }

    });

    card.addEventListener("mouseleave", function () {

        const icon = card.querySelector(".feature-icon");

        if (icon) {
            icon.style.transform = "";
        }

    });

});


// =========================================================
// PREVENT PLACEHOLDER LINKS FROM JUMPING PAGE
// =========================================================

const placeholderLinks = document.querySelectorAll('a[href="#!"]');

placeholderLinks.forEach(function (link) {

    link.addEventListener("click", function (event) {
        event.preventDefault();
    });

});


// =========================================================
// OPTIONAL KEYBOARD ACCESSIBILITY
// =========================================================

document.addEventListener("keydown", function (event) {

    /*
     * Pressing "Home" key moves user to top.
     */

    if (event.key === "Home") {

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    }

});