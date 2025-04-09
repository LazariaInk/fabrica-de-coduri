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