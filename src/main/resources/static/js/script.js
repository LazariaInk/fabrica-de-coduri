document.addEventListener('DOMContentLoaded', function () {
    // Navbar solid on scroll
    window.addEventListener('scroll', function () {
        const nav = document.querySelector('nav');
        if (window.scrollY > 50) {
            nav.classList.add('solid');
        } else {
            nav.classList.remove('solid');
        }
    });

    // Hamburger menu toggle
    document.querySelector('.hamburger').onclick = function () {
        document.querySelector('.menu').classList.toggle('active');
    };

    // Parallax effect
    document.addEventListener('mousemove', function (e) {
        document.querySelectorAll('.parallax-banner img').forEach(layer => {
            const speed = layer.getAttribute('data-speed');
            const translateX = (window.innerWidth - e.clientX * 2) / 100 * speed;
            const translateY = (window.innerHeight - e.clientY * 2) / 100 * speed;
            const scale = 1 + (speed / 20);
            layer.style.transform = `translate(${translateX}px, ${translateY}px) scale(${scale})`;
        });
    });

    // Mobile sidebar auto-close
    const links = document.querySelectorAll('#mobileSidebar a');
    links.forEach(link => {
        link.addEventListener('click', () => {
            document.getElementById('mobileSidebar').classList.remove('active');
        });
    });

    // Show cookie banner if no decision made
    const cookieBanner = document.getElementById("cookieConsentContainer");
    if (!checkCookiesAccepted() && !checkCookiesDeclined() && cookieBanner) {
        cookieBanner.style.display = "flex";
    }
});

// Cookie consent logic
function checkCookiesAccepted() {
    return localStorage.getItem("cookiesConsent") === "accepted";
}

function checkCookiesDeclined() {
    return localStorage.getItem("cookiesConsent") === "declined";
}

function acceptCookies() {
    localStorage.setItem("cookiesConsent", "accepted");
    document.getElementById("cookieConsentContainer").style.display = "none";

    // Aici poți încărca scripturi precum AdSense
    // loadAdSense();
}

function declineCookies() {
    localStorage.setItem("cookiesConsent", "declined");
    document.getElementById("cookieConsentContainer").style.display = "none";
}

function resetCookieConsent() {
    localStorage.removeItem("cookiesConsent");
    location.reload();
}

// Draggable top nav
const nav = document.querySelector('.top-nav');
let isDown = false;
let startX;
let scrollLeft;

nav.addEventListener('mousedown', (e) => {
    isDown = true;
    nav.classList.add('dragging');
    startX = e.pageX - nav.offsetLeft;
    scrollLeft = nav.scrollLeft;
});

nav.addEventListener('mouseleave', () => {
    isDown = false;
    nav.classList.remove('dragging');
});

nav.addEventListener('mouseup', () => {
    isDown = false;
    nav.classList.remove('dragging');
});

nav.addEventListener('mousemove', (e) => {
    if (!isDown) return;
    e.preventDefault();
    const x = e.pageX - nav.offsetLeft;
    const walk = (x - startX) * 2;
    nav.scrollLeft = scrollLeft - walk;
});

// Sidebar toggles
function toggleSidebar() {
    const sidebar = document.getElementById("mobileSidebar");
    sidebar.classList.toggle("active");
}

function toggleMobileSidebar() {
    document.getElementById('mobileSidebar').classList.toggle('active');
}

// Chapters
function toggleChapter(el) {
    const chapterItem = el.parentElement;
    chapterItem.classList.toggle("open");
}

function toggleLessons(element) {
    const chapterItem = element.closest('.chapter-item');
    chapterItem.classList.toggle('open');
}

// Navigation to topic
function goToTopic(element) {
    const url = element.getAttribute('data-topic-url');
    if (url) {
        window.location.href = url;
    }
}

// Donation popup
function openPopup() {
    document.getElementById("donationOverlay").style.display = "flex";
}

function closePopup() {
    document.getElementById("donationOverlay").style.display = "none";
}
