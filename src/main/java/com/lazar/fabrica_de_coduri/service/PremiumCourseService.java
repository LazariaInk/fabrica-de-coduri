package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.CourseComment;
import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.model.UserCourseProgress;
import com.lazar.fabrica_de_coduri.repository.CourseCommentRepository;
import com.lazar.fabrica_de_coduri.repository.PremiumCourseRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import com.lazar.fabrica_de_coduri.repository.UserCourseProgressRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PremiumCourseService {
    private final PremiumCourseRepository premiumCourseRepository;
    private final UserRepository userRepository;
    private final UserCourseProgressRepository userCourseProgressRepository;
    private final CourseCommentRepository courseCommentRepository;

    public PremiumCourseService(PremiumCourseRepository premiumCourseRepository,
                                UserRepository userRepository,
                                UserCourseProgressRepository userCourseProgressRepository,
                                CourseCommentRepository courseCommentRepository) {
        this.premiumCourseRepository = premiumCourseRepository;
        this.userRepository = userRepository;
        this.userCourseProgressRepository = userCourseProgressRepository;
        this.courseCommentRepository = courseCommentRepository;
    }

    @PostConstruct
    @Transactional
    public void seedCourses() {
        List<PremiumCourse> seedCourses = List.of(
                new PremiumCourse(
                        "java-spring-boot-complet",
                        "Java Spring Boot Complet",
                        "Construieste aplicatii web reale cu Spring Boot, Security, JPA si Thymeleaf.",
                        "Intermediar",
                        "Java",
                        "12 ore",
                        "Fabrica de Coduri",
                        64,
                        199,
                        "#00c875",
                        List.of("REST API si MVC", "Autentificare cu Spring Security", "Baze de date cu JPA", "Deploy pregatit pentru productie"),
                        List.of("Setup si arhitectura", "CRUD complet", "Login si roluri", "Proiect final")
                ),
                new PremiumCourse(
                        "python-automatizari-ai",
                        "Python pentru Automatizari si AI",
                        "Invata sa automatizezi task-uri, sa procesezi fisiere si sa folosesti API-uri moderne.",
                        "Incepator",
                        "Python",
                        "9 ore",
                        "Fabrica de Coduri",
                        48,
                        149,
                        "#38bdf8",
                        List.of("Scripturi utile", "Lucru cu fisiere si API-uri", "Dashboard simplu", "Introducere practica in AI"),
                        List.of("Bazele Python aplicate", "Automatizari", "API-uri", "Mini-proiect AI")
                ),
                new PremiumCourse(
                        "frontend-react-pro",
                        "Frontend React Pro",
                        "De la componente curate la aplicatii rapide, responsive si usor de mentinut.",
                        "Avansat",
                        "JavaScript",
                        "15 ore",
                        "Fabrica de Coduri",
                        72,
                        249,
                        "#f97316",
                        List.of("React modern", "State management", "Consum API", "UI responsive si profesional"),
                        List.of("Fundamente React", "Componente si hooks", "Aplicatie completa", "Optimizare")
                )
        );

        for (PremiumCourse seedCourse : seedCourses) {
            premiumCourseRepository.findBySlug(seedCourse.getSlug())
                    .ifPresentOrElse(existingCourse -> backfillSeedMetadata(existingCourse, seedCourse),
                            () -> premiumCourseRepository.save(seedCourse));
        }
    }

    @Transactional(readOnly = true)
    public List<PremiumCourse> findAll(String query, String language, String view, String username) {
        List<PremiumCourse> courses = premiumCourseRepository.findAll();
        String normalizedQuery = normalize(query);
        String normalizedLanguage = normalize(language);
        String normalizedView = normalize(view);
        Set<String> purchasedSlugs = "owned".equals(normalizedView) ? findPurchasedCourseSlugs(username) : Set.of();
        Set<String> wishlistSlugs = "wishlist".equals(normalizedView) ? findWishlistCourseSlugs(username) : Set.of();

        return courses.stream()
                .filter(course -> normalizedLanguage == null
                        || normalizedLanguage.equals(normalize(course.getLanguage())))
                .filter(course -> !"owned".equals(normalizedView) || purchasedSlugs.contains(course.getSlug()))
                .filter(course -> !"wishlist".equals(normalizedView) || wishlistSlugs.contains(course.getSlug()))
                .filter(course -> normalizedQuery == null
                        || course.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || course.getSubtitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || course.getLevel().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || containsNormalized(course.getLanguage(), normalizedQuery))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PremiumCourse> findAll(String query, String language) {
        return findAll(query, language, "all", null);
    }

    @Transactional(readOnly = true)
    public List<String> findLanguages() {
        return premiumCourseRepository.findAll().stream()
                .map(PremiumCourse::getLanguage)
                .filter(language -> language != null && !language.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    private void backfillSeedMetadata(PremiumCourse existingCourse, PremiumCourse seedCourse) {
        if (existingCourse.getLanguage() == null || existingCourse.getLanguage().isBlank()) {
            existingCourse.setLanguage(seedCourse.getLanguage());
            premiumCourseRepository.save(existingCourse);
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.toLowerCase(Locale.ROOT).trim();
    }

    private boolean containsNormalized(String value, String normalizedQuery) {
        String normalizedValue = normalize(value);
        return normalizedValue != null && normalizedValue.contains(normalizedQuery);
    }

    @Transactional(readOnly = true)
    public Optional<PremiumCourse> findBySlug(String slug) {
        return premiumCourseRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public Set<String> findPurchasedCourseSlugs(String username) {
        if (username == null) {
            return Set.of();
        }

        return userRepository.findByUsername(username)
                .map(user -> user.getPurchasedCourses().stream()
                        .map(PremiumCourse::getSlug)
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
    }

    @Transactional(readOnly = true)
    public Set<String> findWishlistCourseSlugs(String username) {
        if (username == null) {
            return Set.of();
        }

        return userRepository.findByUsername(username)
                .map(user -> user.getWishlistCourses().stream()
                        .map(PremiumCourse::getSlug)
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> findProgressPercentByCourseSlug(String username) {
        if (username == null) {
            return Map.of();
        }

        return userRepository.findByUsername(username)
                .map(user -> userCourseProgressRepository.findByUser(user).stream()
                        .collect(Collectors.toMap(
                                progress -> progress.getCourse().getSlug(),
                                progress -> calculateProgressPercent(progress.getCompletedLessons(), progress.getCourse().getLessons())
                        )))
                .orElse(Map.of());
    }

    @Transactional(readOnly = true)
    public int findProgressPercent(String username, String slug) {
        return findProgressPercentByCourseSlug(username).getOrDefault(slug, 0);
    }

    @Transactional(readOnly = true)
    public int findCompletedLessons(String username, String slug) {
        if (username == null) {
            return 0;
        }

        return userRepository.findByUsername(username)
                .flatMap(user -> premiumCourseRepository.findBySlug(slug)
                        .flatMap(course -> userCourseProgressRepository.findByUserAndCourse(user, course)))
                .map(UserCourseProgress::getCompletedLessons)
                .orElse(0);
    }

    @Transactional(readOnly = true)
    public boolean hasPurchasedCourse(String username, String slug) {
        return findPurchasedCourseSlugs(username).contains(slug);
    }

    @Transactional(readOnly = true)
    public List<CourseComment> findComments(String slug) {
        PremiumCourse course = premiumCourseRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return courseCommentRepository.findByCourseOrderByUpdatedAtDesc(course);
    }

    @Transactional(readOnly = true)
    public Optional<CourseComment> findCommentForUser(String username, String slug) {
        if (username == null) {
            return Optional.empty();
        }

        return userRepository.findByUsername(username)
                .flatMap(user -> premiumCourseRepository.findBySlug(slug)
                        .flatMap(course -> courseCommentRepository.findByUserAndCourse(user, course)));
    }

    @Transactional
    public PremiumCourse purchaseCourse(String username, String slug) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PremiumCourse course = premiumCourseRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        user.getPurchasedCourses().add(course);
        user.getWishlistCourses().remove(course);
        userRepository.save(user);
        ensureProgress(user, course);
        return course;
    }

    @Transactional
    public boolean toggleWishlistCourse(String username, String slug) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PremiumCourse course = premiumCourseRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (user.getPurchasedCourses().contains(course)) {
            return false;
        }

        boolean added;
        if (user.getWishlistCourses().contains(course)) {
            user.getWishlistCourses().remove(course);
            added = false;
        } else {
            user.getWishlistCourses().add(course);
            added = true;
        }

        userRepository.save(user);
        return added;
    }

    @Transactional
    public int completeNextLesson(String username, String slug) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PremiumCourse course = premiumCourseRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!user.getPurchasedCourses().contains(course)) {
            throw new IllegalArgumentException("Course not purchased");
        }

        UserCourseProgress progress = ensureProgress(user, course);
        progress.setCompletedLessons(Math.min(progress.getCompletedLessons() + 1, course.getLessons()));
        userCourseProgressRepository.save(progress);
        return progress.getCompletedLessons();
    }

    @Transactional
    public CourseComment saveComment(String username, String slug, String content) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PremiumCourse course = premiumCourseRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!user.getPurchasedCourses().contains(course)) {
            throw new IllegalArgumentException("Course not purchased");
        }

        String normalizedContent = content == null ? "" : content.trim();
        if (normalizedContent.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        if (normalizedContent.length() > 1200) {
            normalizedContent = normalizedContent.substring(0, 1200);
        }

        CourseComment comment = courseCommentRepository.findByUserAndCourse(user, course)
                .orElseGet(() -> {
                    CourseComment newComment = new CourseComment();
                    newComment.setUser(user);
                    newComment.setCourse(course);
                    return newComment;
                });
        comment.setContent(normalizedContent);
        return courseCommentRepository.save(comment);
    }

    private UserCourseProgress ensureProgress(User user, PremiumCourse course) {
        return userCourseProgressRepository.findByUserAndCourse(user, course)
                .orElseGet(() -> {
                    UserCourseProgress progress = new UserCourseProgress();
                    progress.setUser(user);
                    progress.setCourse(course);
                    progress.setCompletedLessons(0);
                    return userCourseProgressRepository.save(progress);
                });
    }

    private int calculateProgressPercent(int completedLessons, int totalLessons) {
        if (totalLessons <= 0) {
            return 0;
        }

        return Math.min(100, (int) Math.round((completedLessons * 100.0) / totalLessons));
    }
}
