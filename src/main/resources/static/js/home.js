(function () {
    "use strict";

    var bound = false;

    function reducedMotion() {
        return window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    }

    function onHomePage() {
        return Boolean(document.getElementById("site-nav") && document.getElementById("features"));
    }

    function headerEl() {
        return document.querySelector(".site-header");
    }

    function closeNav() {
        var header = headerEl();
        if (!header) return;
        var nav = document.getElementById("site-nav");
        var toggle = header.querySelector(".nav-toggle");
        if (nav) nav.classList.remove("is-open");
        if (toggle) toggle.setAttribute("aria-expanded", "false");
    }

    function setActive(id) {
        var links = document.querySelectorAll("[data-nav]");
        Array.prototype.forEach.call(links, function (link) {
            var on = link.getAttribute("data-nav") === id;
            link.classList.toggle("is-active", on);
            if (on) link.setAttribute("aria-current", "page");
            else link.removeAttribute("aria-current");
        });
    }

    function updateOnScroll() {
        if (!onHomePage()) return;
        var header = headerEl();
        var toTop = document.querySelector(".to-top");
        var y = window.scrollY || window.pageYOffset || 0;
        if (header) header.classList.toggle("is-scrolled", y > 4);
        if (toTop) {
            var show = y > 480;
            toTop.classList.toggle("is-visible", show);
            toTop.tabIndex = show ? 0 : -1;
        }

        var marker = y + (header ? header.offsetHeight : 0) + 32;
        var current = "home";
        ["about", "features", "how-it-works", "contact"].forEach(function (id) {
            var section = document.getElementById(id);
            if (section && section.offsetTop <= marker) current = id;
        });
        setActive(current);
    }

    function scrollToTarget(el) {
        el.scrollIntoView({
            behavior: reducedMotion() ? "auto" : "smooth",
            block: "start"
        });
    }

    function onClick(event) {
        if (!onHomePage()) return;
        var topButton = event.target.closest(".to-top");
        if (topButton) {
            event.preventDefault();
            window.scrollTo({ top: 0, behavior: reducedMotion() ? "auto" : "smooth" });
            return;
        }

        var toggle = event.target.closest(".nav-toggle");
        if (toggle) {
            var nav = document.getElementById("site-nav");
            if (!nav) return;
            var open = !nav.classList.contains("is-open");
            nav.classList.toggle("is-open", open);
            toggle.setAttribute("aria-expanded", open ? "true" : "false");
            return;
        }

        var homeLink = event.target.closest("[data-nav='home']");
        if (homeLink) {
            event.preventDefault();
            window.scrollTo({ top: 0, behavior: reducedMotion() ? "auto" : "smooth" });
            closeNav();
            return;
        }

        var hashLink = event.target.closest("a[href^='#']");
        if (hashLink) {
            var id = hashLink.getAttribute("href").slice(1);
            var target = document.getElementById(id);
            if (!target) return;
            event.preventDefault();
            scrollToTarget(target);
            closeNav();
            return;
        }

        if (!event.target.closest("#site-nav") && !event.target.closest(".nav-toggle")) {
            closeNav();
        }
    }

    function onKey(event) {
        if (event.key === "Escape") closeNav();
    }

    function onResize() {
        if (window.innerWidth >= 900) closeNav();
    }

    function init() {
        if (!onHomePage()) return;
        if (!bound) {
            bound = true;
            document.addEventListener("click", onClick);
            document.addEventListener("keydown", onKey);
            window.addEventListener("scroll", updateOnScroll, { passive: true });
            window.addEventListener("resize", onResize);
        }
        updateOnScroll();
    }

    window.FundAxisHome = { init: init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();