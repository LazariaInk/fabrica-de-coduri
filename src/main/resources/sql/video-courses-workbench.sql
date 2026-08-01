-- Inspectare rapida pentru cursuri video premium.
-- Ruleaza pe schema: fabricadecoduri

SELECT
    pc.id,
    pc.slug,
    pc.title,
    pc.discord_url,
    pc.duration,
    pc.lessons,
    COUNT(cv.id) AS videos_in_db,
    COALESCE(ROUND(SUM(cv.duration_seconds) / 3600, 2), 0) AS video_hours
FROM premium_courses pc
LEFT JOIN course_videos cv ON cv.premium_course_id = pc.id
GROUP BY pc.id, pc.slug, pc.title, pc.discord_url, pc.duration, pc.lessons
ORDER BY pc.title;

SELECT
    pc.slug AS course_slug,
    cv.id AS video_id,
    cv.position,
    cv.title,
    cv.duration_seconds,
    cv.storage_key
FROM course_videos cv
JOIN premium_courses pc ON pc.id = cv.premium_course_id
ORDER BY pc.slug, cv.position;

SELECT
    u.id AS user_id,
    u.username,
    pc.slug AS purchased_course,
    pc.title
FROM user_premium_courses upc
JOIN users u ON u.id = upc.user_id
JOIN premium_courses pc ON pc.id = upc.premium_course_id
ORDER BY u.username, pc.title;

SELECT
    u.username,
    pc.slug AS course_slug,
    cv.position,
    cv.title,
    uvp.watched_seconds,
    cv.duration_seconds,
    uvp.completed
FROM user_video_progress uvp
JOIN users u ON u.id = uvp.user_id
JOIN course_videos cv ON cv.id = uvp.course_video_id
JOIN premium_courses pc ON pc.id = cv.premium_course_id
ORDER BY u.username, pc.slug, cv.position;

-- Exemplu: cumperi manual un curs pentru userul logat, pentru test local.
-- Schimba valorile username/slug.
INSERT IGNORE INTO user_premium_courses (user_id, premium_course_id)
SELECT u.id, pc.id
FROM users u
JOIN premium_courses pc ON pc.slug = 'java-spring-boot-complet'
WHERE u.username = 'email-sau-username';

-- Exemplu: setezi/actualizezi fisierul video 1 pentru un curs.
-- Pune fisierul aici in proiect: secure-videos/java-spring-boot-complet/video-1.mp4
UPDATE course_videos cv
JOIN premium_courses pc ON pc.id = cv.premium_course_id
SET cv.storage_key = 'java-spring-boot-complet/video-1.mp4',
    cv.duration_seconds = 600
WHERE pc.slug = 'java-spring-boot-complet'
  AND cv.position = 1;

-- Exemplu: setezi canalul Discord pentru un curs premium.
UPDATE premium_courses
SET discord_url = 'https://discord.gg/J5mK6cyTQC'
WHERE slug = 'frontend-react-pro';

-- Test recomandat cu 10 video-uri:
-- Pune mock-urile aici:
-- secure-videos/frontend-react-pro/video-1.mp4
-- secure-videos/frontend-react-pro/video-2.mp4
-- ...
-- secure-videos/frontend-react-pro/video-10.mp4

INSERT IGNORE INTO user_premium_courses (user_id, premium_course_id)
SELECT u.id, pc.id
FROM users u
JOIN premium_courses pc ON pc.slug = 'frontend-react-pro'
WHERE u.username = 'lorienlored@gmail.com';

SELECT
    cv.position,
    cv.title,
    cv.duration_seconds,
    cv.storage_key
FROM course_videos cv
JOIN premium_courses pc ON pc.id = cv.premium_course_id
WHERE pc.slug = 'frontend-react-pro'
ORDER BY cv.position;
