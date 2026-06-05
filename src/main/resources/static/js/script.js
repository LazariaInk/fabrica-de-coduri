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

    document.addEventListener('click', function (event) {
        const accountMenu = document.querySelector('.account-menu');
        if (accountMenu && !accountMenu.contains(event.target)) {
            accountMenu.classList.remove('open');
        }
    });

    const avatarDropzone = document.querySelector('.avatar-dropzone');
    const avatarInput = document.getElementById('avatarUpload');
    const avatarPreview = document.getElementById('avatarPreview');
    if (avatarDropzone && avatarInput && avatarPreview) {
        avatarInput.addEventListener('change', function () {
            previewAvatarFile(avatarInput.files[0], avatarPreview, avatarDropzone);
        });

        avatarDropzone.addEventListener('dragover', function (event) {
            event.preventDefault();
            avatarDropzone.classList.add('dragging');
        });

        avatarDropzone.addEventListener('dragleave', function () {
            avatarDropzone.classList.remove('dragging');
        });

        avatarDropzone.addEventListener('drop', function (event) {
            event.preventDefault();
            avatarDropzone.classList.remove('dragging');
            const file = event.dataTransfer.files[0];
            if (!file) return;

            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(file);
            avatarInput.files = dataTransfer.files;
            previewAvatarFile(file, avatarPreview, avatarDropzone);
        });
    }

    // Parallax effect
    const parallaxImages = document.querySelectorAll('.parallax-banner img');
    if (parallaxImages.length > 0) {
        document.addEventListener('mousemove', function (e) {
            parallaxImages.forEach(layer => {
                const speed = layer.getAttribute('data-speed');
                const translateX = (window.innerWidth - e.clientX * 2) / 100 * speed;
                const translateY = (window.innerHeight - e.clientY * 2) / 100 * speed;
                const scale = 1 + (speed / 20);
                layer.style.transform = `translate(${translateX}px, ${translateY}px) scale(${scale})`;
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

function toggleAccountMenu(event) {
    event.stopPropagation();
    const accountMenu = event.currentTarget.closest('.account-menu');
    if (accountMenu) accountMenu.classList.toggle('open');
}

function previewAvatarFile(file, preview, dropzone) {
    if (!file || !file.type.startsWith('image/')) return;

    const reader = new FileReader();
    reader.onload = function (event) {
        preview.src = event.target.result;
        preview.style.display = 'block';
        dropzone.classList.add('has-preview');
    };
    reader.readAsDataURL(file);
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

