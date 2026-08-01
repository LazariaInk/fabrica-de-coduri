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

    initCourseVideoPlayer();
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

function initCourseVideoPlayer() {
    const player = document.querySelector('.secure-video-player');
    const playlistButtons = document.querySelectorAll('.playlist-item[data-src]');
    if (!player || playlistButtons.length === 0) return;

    const source = player.querySelector('source');
    const title = document.querySelector('.current-video-title');
    const completedLessonsLabel = document.querySelector('.completed-lessons-label');
    const percentLabels = document.querySelectorAll('.course-progress-label strong');
    const percentBars = document.querySelectorAll('.course-progress-track span');
    const csrfInput = document.querySelector('.course-video-csrf');
    let progressUrl = player.dataset.progressUrl;
    let lastSavedSecond = Number(player.dataset.watched || 0);
    let saveTimeout = null;
    let activeButton = null;

    function activateVideo(button) {
        playlistButtons.forEach(item => item.classList.remove('active'));
        button.classList.add('active');
        activeButton = button;
        progressUrl = button.dataset.progressUrl;
        lastSavedSecond = Number(button.dataset.watched || 0);
        if (title) title.textContent = button.dataset.title || 'Lectia curenta';
        if (source && source.src !== button.dataset.src) {
            source.src = button.dataset.src;
            player.load();
        }
    }

    function updateCourseProgress(percent) {
        percentLabels.forEach(label => {
            label.textContent = percent + '%';
        });
        percentBars.forEach(bar => {
            bar.style.width = percent + '%';
        });
    }

    function updateCompletedLessons(completedLessons) {
        if (!completedLessonsLabel) return;

        const totalLessons = completedLessonsLabel.dataset.totalLessons || playlistButtons.length;
        completedLessonsLabel.textContent = completedLessons + ' din ' + totalLessons + ' lectii finalizate';
    }

    function saveProgress(force) {
        if (!progressUrl || !Number.isFinite(player.currentTime)) return;

        const videoDuration = Number.isFinite(player.duration) && player.duration > 0
            ? Math.max(1, Math.round(player.duration))
            : 0;
        const watchedSeconds = player.ended && videoDuration > 0
            ? videoDuration
            : Math.floor(player.currentTime);
        if (!force && watchedSeconds - lastSavedSecond < 10) return;
        lastSavedSecond = Math.max(lastSavedSecond, watchedSeconds);
        if (activeButton) activeButton.dataset.watched = String(lastSavedSecond);

        const body = new URLSearchParams();
        body.set('watchedSeconds', String(lastSavedSecond));
        if (videoDuration > 0) {
            body.set('durationSeconds', String(videoDuration));
        }
        if (csrfInput) body.set(csrfInput.name, csrfInput.value);

        fetch(progressUrl, {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body
        })
            .then(response => response.ok ? response.json() : null)
            .then(data => {
                if (data && typeof data.progressPercent === 'number') {
                    updateCourseProgress(data.progressPercent);
                }
                if (data && typeof data.completedLessons === 'number') {
                    updateCompletedLessons(data.completedLessons);
                }
            })
            .catch(() => {});
    }

    playlistButtons.forEach(button => {
        button.addEventListener('click', () => activateVideo(button));
    });

    player.addEventListener('loadedmetadata', () => {
        if (lastSavedSecond > 0 && player.duration && lastSavedSecond < player.duration - 2) {
            player.currentTime = lastSavedSecond;
        }
    });
    player.addEventListener('timeupdate', () => {
        clearTimeout(saveTimeout);
        saveTimeout = setTimeout(() => saveProgress(false), 300);
    });
    player.addEventListener('pause', () => saveProgress(true));
    player.addEventListener('ended', () => saveProgress(true));

    activateVideo(playlistButtons[0]);
}

