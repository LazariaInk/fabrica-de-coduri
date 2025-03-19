document.addEventListener('DOMContentLoaded', function () {
    window.addEventListener('scroll', function () {
        const nav = document.querySelector('nav');
        if (window.scrollY > 50) {
            nav.classList.add('solid');
        } else {
            nav.classList.remove('solid');
        }
    });

    document.querySelector('.hamburger').onclick = function () {
        document.querySelector('.menu').classList.toggle('active');
    };

    document.addEventListener('mousemove', function (e) {
        document.querySelectorAll('.parallax-banner img').forEach(layer => {
            const speed = layer.getAttribute('data-speed');
            const translateX = (window.innerWidth - e.clientX * 2) / 100 * speed;
            const translateY = (window.innerHeight - e.clientY * 2) / 100 * speed;
            const scale = 1 + (speed / 20);
            layer.style.transform = `translate(${translateX}px, ${translateY}px) scale(${scale})`;
        });
    });
});

function toggleTheme() {
    const body = document.body;
    const themeIcon = document.querySelector('.theme-icon');

    body.classList.toggle('light-mode');
    body.classList.toggle('dark-mode');

    if (body.classList.contains('light-mode')) {
        localStorage.setItem('theme', 'light');
        themeIcon.textContent = '☀️';
    } else {
        localStorage.setItem('theme', 'dark');
        themeIcon.textContent = '🌙';
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const theme = localStorage.getItem('theme');
    const themeIcon = document.querySelector('.theme-icon');

    if (theme === 'light') {
        document.body.classList.add('light-mode');
        document.body.classList.remove('dark-mode');
        themeIcon.textContent = '☀️';
    } else {
        document.body.classList.add('dark-mode');
        document.body.classList.remove('light-mode');
        themeIcon.textContent = '🌙';
    }
});