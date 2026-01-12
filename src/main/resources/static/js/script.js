document.addEventListener('DOMContentLoaded', function () {
    // Scroll navbar solid background
    const navBar = document.querySelector('nav');
    if (navBar) {
        window.addEventListener('scroll', function () {
            if (window.scrollY > 50) {
                navBar.classList.add('solid');
            } else {
                navBar.classList.remove('solid');
            }
        });
    }

    // Hamburger menu toggle
    const hamburger = document.querySelector('.hamburger');
    const menu = document.querySelector('.menu');
    if (hamburger && menu) {
        hamburger.onclick = function () {
            menu.classList.toggle('active');
        };
    }

// Parallax effect
const parallaxImages = document.querySelectorAll('.floating-icon img');

if (parallaxImages.length > 0) {
    document.addEventListener('mousemove', function (e) {
        const percentX = e.clientX / window.innerWidth;
        const percentY = e.clientY / window.innerHeight;

        parallaxImages.forEach(layer => {
            const speed = parseFloat(layer.getAttribute('data-speed')) || 1;

            const moveX = (0.5 - percentX) * speed * 20;
            const moveY = (0.5 - percentY) * speed * 20;

            layer.style.transform = `translate(${moveX}px, ${moveY}px)`;
        });
    });
}


    // Mobile sidebar close on link click
    const links = document.querySelectorAll('#mobileSidebar a');
    links.forEach(link => {
        link.addEventListener('click', () => {
            const sidebar = document.getElementById('mobileSidebar');
            if (sidebar) sidebar.classList.remove('active');
        });
    });

    // Fix scroll bubbling in fixed sidebar
    const fixedSidebar = document.querySelector('.sidebar');
        if (fixedSidebar) {
            fixedSidebar.addEventListener('wheel', function (e) {
                const atTop = fixedSidebar.scrollTop === 0;
                const atBottom = fixedSidebar.scrollHeight - fixedSidebar.clientHeight === fixedSidebar.scrollTop;

                if ((e.deltaY < 0 && atTop) || (e.deltaY > 0 && atBottom)) {
                    e.preventDefault(); // Blocheaza propagarea spre body
                }
            }, { passive: false });
        }
});

// Scrollable .top-nav drag support (only if exists)
const topNav = document.querySelector('.top-nav');
if (topNav) {
    let isDown = false;
    let startX;
    let scrollLeft;

    topNav.addEventListener('mousedown', (e) => {
        isDown = true;
        topNav.classList.add('dragging');
        startX = e.pageX - topNav.offsetLeft;
        scrollLeft = topNav.scrollLeft;
    });

    topNav.addEventListener('mouseleave', () => {
        isDown = false;
        topNav.classList.remove('dragging');
    });

    topNav.addEventListener('mouseup', () => {
        isDown = false;
        topNav.classList.remove('dragging');
    });

    topNav.addEventListener('mousemove', (e) => {
        if (!isDown) return;
        e.preventDefault();
        const x = e.pageX - topNav.offsetLeft;
        const walk = (x - startX) * 2;
        topNav.scrollLeft = scrollLeft - walk;
    });
}

// Sidebar height adjustment
window.addEventListener('scroll', function () {
    const sidebar = document.querySelector('.sidebar');
    if (!sidebar) return;

    const footerHeight = 406;
    const scrollY = window.scrollY;
    const documentHeight = document.body.scrollHeight;
    const windowHeight = window.innerHeight;
    const distanceFromBottom = documentHeight - (scrollY + windowHeight);

    if (distanceFromBottom < footerHeight) {
        const overlap = footerHeight - distanceFromBottom;
        const newMaxHeight = windowHeight - overlap;
        sidebar.style.maxHeight = `${newMaxHeight}px`;
    } else {
        sidebar.style.maxHeight = `${windowHeight}px`;
    }
});

// Utility functions
function toggleSidebar() {
    const sidebar = document.getElementById("mobileSidebar");
    if (sidebar) sidebar.classList.toggle("active");
}

function toggleMobileSidebar() {
    const sidebar = document.getElementById("mobileSidebar");
    if (sidebar) sidebar.classList.toggle('active');
}

function toggleChapter(el) {
    const chapterItem = el.parentElement;
    if (chapterItem) chapterItem.classList.toggle("open");
}

function toggleLessons(element) {
    const chapterItem = element.closest('.chapter-item');
    if (chapterItem) chapterItem.classList.toggle('open');
}

function goToTopic(element) {
    const url = element.getAttribute('data-topic-url');
    if (url) {
        window.location.href = url;
    }
}

function openPopup() {
    const overlay = document.getElementById("donationOverlay");
    if (overlay) overlay.style.display = "flex";
}

function closePopup() {
    const overlay = document.getElementById("donationOverlay");
    if (overlay) overlay.style.display = "none";
}

document.addEventListener("DOMContentLoaded", function () {
  const bannerHTML = `
    <div id="cookieConsentBanner">
      <div class="cookie-box">
        <h3>Respectăm intimitatea ta</h3>
        <p class="cookie-text">
          Folosim cookie-uri pentru a personaliza conținutul și reclamele, pentru a oferi funcționalități media sociale și pentru a analiza traficul nostru.
          <br><br>
          <a href="/politica-confidentialitate" target="_blank">Află mai multe</a>
        </p>
        <div class="cookie-actions">
          <button id="acceptCookies">Accept</button>
          <button id="rejectCookies">Refuz</button>
        </div>
      </div>
    </div>
  `;
  document.body.insertAdjacentHTML('beforeend', bannerHTML);

  const saved = localStorage.getItem('cookieConsent');
  if (saved === 'granted') {
    acceptConsent();
  } else if (saved === 'denied') {
    rejectConsent();
  } else {
    document.getElementById('cookieConsentBanner').style.display = 'block';
  }

  document.getElementById('acceptCookies').addEventListener('click', acceptConsent);
  document.getElementById('rejectCookies').addEventListener('click', rejectConsent);
});

function acceptConsent() {
  gtag('consent', 'update', {
    'ad_storage': 'granted',
    'analytics_storage': 'granted',
    'ad_user_data': 'granted',
    'ad_personalization': 'granted'
  });
  localStorage.setItem('cookieConsent', 'granted');
  const banner = document.getElementById('cookieConsentBanner');
  if (banner) banner.style.display = 'none';
}

function rejectConsent() {
  gtag('consent', 'update', {
    'ad_storage': 'denied',
    'analytics_storage': 'denied',
    'ad_user_data': 'denied',
    'ad_personalization': 'denied'
  });
  localStorage.setItem('cookieConsent', 'denied');
  const banner = document.getElementById('cookieConsentBanner');
  if (banner) banner.style.display = 'none';
}

