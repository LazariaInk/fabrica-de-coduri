package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.CourseComment;
import com.lazar.fabrica_de_coduri.model.CourseAuthor;
import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.model.UserCourseProgress;
import com.lazar.fabrica_de_coduri.repository.CourseCommentRepository;
import com.lazar.fabrica_de_coduri.repository.CourseAuthorRepository;
import com.lazar.fabrica_de_coduri.repository.PremiumCourseRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import com.lazar.fabrica_de_coduri.repository.UserCourseProgressRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
    private final CourseAuthorRepository courseAuthorRepository;

    public PremiumCourseService(PremiumCourseRepository premiumCourseRepository,
                                UserRepository userRepository,
                                UserCourseProgressRepository userCourseProgressRepository,
                                CourseCommentRepository courseCommentRepository,
                                CourseAuthorRepository courseAuthorRepository) {
        this.premiumCourseRepository = premiumCourseRepository;
        this.userRepository = userRepository;
        this.userCourseProgressRepository = userCourseProgressRepository;
        this.courseCommentRepository = courseCommentRepository;
        this.courseAuthorRepository = courseAuthorRepository;
    }

    @PostConstruct
    @Transactional
    public void seedCourses() {
        CourseAuthor fabricaDeCoduri = saveAuthor(new CourseAuthor(
                "fabrica-de-coduri",
                "Fabrica de Coduri",
                "Echipa editoriala care transforma conceptele grele in lectii clare, aplicate si in romana.",
                "Fabrica de Coduri construieste cursuri pentru oameni care vor sa invete programare fara zgomot inutil. Lectiile sunt gandite pentru practica: cod scris pas cu pas, proiecte mici care cresc natural si explicatii care conecteaza teoria cu munca reala.",
                "https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=500&q=80",
                "Romania",
                "8+ ani continut educational",
                "4.9",
                "12.000+ cursanti",
                List.of("Java", "Spring Boot", "Thymeleaf", "Arhitectura web"),
                "",
                "https://github.com/",
                "https://www.fabricadecoduri.com"
        ));
        CourseAuthor anaPopescu = saveAuthor(new CourseAuthor(
                "ana-popescu",
                "Ana Popescu",
                "Frontend engineer specializata in React, UI systems si aplicatii rapide pentru productie.",
                "Ana lucreaza cu echipe de produs care au nevoie de interfete curate, scalabile si usor de mentinut. In cursurile ei pune accent pe componente reutilizabile, accesibilitate, performanta si fluxuri de lucru apropiate de proiectele reale.",
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=500&q=80",
                "Cluj-Napoca",
                "7 ani frontend",
                "4.8",
                "6.400+ cursanti",
                List.of("React", "TypeScript", "Design systems", "UX pentru dashboard-uri"),
                "https://www.linkedin.com/",
                "https://github.com/",
                ""
        ));
        CourseAuthor raduIonescu = saveAuthor(new CourseAuthor(
                "radu-ionescu",
                "Radu Ionescu",
                "Backend developer pasionat de API-uri, baze de date si automatizari cu Python.",
                "Radu construieste sisteme backend robuste si explica simplu deciziile care conteaza: modelare de date, securitate, integrare cu API-uri externe si deploy. Cursurile lui sunt directe, practice si orientate pe proiecte.",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80",
                "Bucuresti",
                "9 ani backend",
                "4.9",
                "8.100+ cursanti",
                List.of("Python", "REST API", "SQL", "Automatizari"),
                "https://www.linkedin.com/",
                "https://github.com/",
                ""
        ));
        CourseAuthor maraStan = saveAuthor(new CourseAuthor(
                "mara-stan",
                "Mara Stan",
                "Mentor full-stack pentru incepatori care vor proiecte clare si explicatii rabdatoare.",
                "Mara ajuta cursantii sa treaca de la tutoriale izolate la aplicatii complete. Stilul ei este calm si vizual, cu multe exemple, recapitulare si exercitii care fixeaza conceptele importante.",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=500&q=80",
                "Iasi",
                "6 ani mentorat",
                "4.7",
                "5.300+ cursanti",
                List.of("HTML", "CSS", "JavaScript", "Proiecte pentru portofoliu"),
                "https://www.linkedin.com/",
                "https://github.com/",
                ""
        ));

        List<PremiumCourse> seedCourses = List.of(
                course(
                        "java-spring-boot-complet",
                        "Java Spring Boot Complet",
                        "Construieste aplicatii web reale cu Spring Boot, Security, JPA si Thymeleaf.",
                        "Intermediar",
                        "Java",
                        "12 ore",
                        fabricaDeCoduri,
                        64,
                        199,
                        "#00c875",
                        List.of("REST API si MVC", "Autentificare cu Spring Security", "Baze de date cu JPA", "Deploy pregatit pentru productie"),
                        List.of("Setup si arhitectura", "CRUD complet", "Login si roluri", "Proiect final")
                ),
                course(
                        "python-automatizari-ai",
                        "Python pentru Automatizari si AI",
                        "Invata sa automatizezi task-uri, sa procesezi fisiere si sa folosesti API-uri moderne.",
                        "Incepator",
                        "Python",
                        "9 ore",
                        raduIonescu,
                        48,
                        149,
                        "#38bdf8",
                        List.of("Scripturi utile", "Lucru cu fisiere si API-uri", "Dashboard simplu", "Introducere practica in AI"),
                        List.of("Bazele Python aplicate", "Automatizari", "API-uri", "Mini-proiect AI")
                ),
                course(
                        "frontend-react-pro",
                        "Frontend React Pro",
                        "De la componente curate la aplicatii rapide, responsive si usor de mentinut.",
                        "Avansat",
                        "JavaScript",
                        "15 ore",
                        anaPopescu,
                        72,
                        249,
                        "#f97316",
                        List.of("React modern", "State management", "Consum API", "UI responsive si profesional"),
                        List.of("Fundamente React", "Componente si hooks", "Aplicatie completa", "Optimizare")
                ),
                course(
                        "typescript-pentru-proiecte-reale",
                        "TypeScript pentru Proiecte Reale",
                        "Tipuri, modele de date si refactorizari sigure pentru aplicatii JavaScript moderne.",
                        "Intermediar",
                        "TypeScript",
                        "8 ore",
                        anaPopescu,
                        42,
                        159,
                        "#8b5cf6",
                        List.of("Type safety aplicat", "Tipuri pentru API-uri", "Refactorizari fara frica", "Patterns pentru componente"),
                        List.of("Bazele TypeScript", "Tipuri avansate", "Integrare cu React", "Proiect practic")
                ),
                course(
                        "sql-baze-de-date-practic",
                        "SQL si Baze de Date Practic",
                        "Invata sa modelezi, interoghezi si optimizezi datele din aplicatii reale.",
                        "Incepator",
                        "SQL",
                        "10 ore",
                        raduIonescu,
                        54,
                        139,
                        "#22c55e",
                        List.of("Modelare relationala", "JOIN-uri clare", "Indexi si performanta", "Rapoarte utile"),
                        List.of("Tabele si relatii", "Interogari", "Optimizare", "Mini-proiect dashboard")
                ),
                course(
                        "html-css-layout-modern",
                        "HTML si CSS Layout Modern",
                        "Construieste pagini responsive cu Flexbox, Grid si componente curate.",
                        "Incepator",
                        "HTML",
                        "7 ore",
                        maraStan,
                        38,
                        99,
                        "#06b6d4",
                        List.of("Structura HTML semantica", "Flexbox si Grid", "Responsive design", "UI polish pentru portofoliu"),
                        List.of("HTML semantic", "CSS modern", "Layout responsive", "Pagina finala")
                ),
                course(
                        "javascript-dom-proiecte",
                        "JavaScript DOM prin Proiecte",
                        "Invata evenimente, state local si interactiuni reale construind mini-aplicatii.",
                        "Incepator",
                        "JavaScript",
                        "11 ore",
                        maraStan,
                        58,
                        129,
                        "#eab308",
                        List.of("DOM fara confuzie", "Evenimente si formulare", "State local", "Mini-aplicatii interactive"),
                        List.of("Selectori si evenimente", "Formulare", "Aplicatie quiz", "Aplicatie task manager")
                ),
                course(
                        "spring-security-masterclass",
                        "Spring Security Masterclass",
                        "Login, roluri, OAuth2 si protectii reale pentru aplicatii Spring Boot.",
                        "Avansat",
                        "Java",
                        "13 ore",
                        fabricaDeCoduri,
                        68,
                        229,
                        "#14b8a6",
                        List.of("Security filter chain", "Form login si OAuth2", "CSRF si autorizare", "Protectii pentru productie"),
                        List.of("Fundamente Security", "Autentificare", "OAuth2", "Hardening")
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
        return findFiltered(query, language, view, username, null, null, null, null, null);
    }

    @Transactional(readOnly = true)
    public Page<PremiumCourse> findPage(String query, String language, String view, String username,
                                        Integer minPrice, Integer maxPrice, Integer maxDuration,
                                        String level, String sort, int page, int size) {
        List<PremiumCourse> filteredCourses = findFiltered(query, language, view, username, minPrice, maxPrice,
                maxDuration, level, sort);
        return page(filteredCourses, page, size);
    }

    private List<PremiumCourse> findFiltered(String query, String language, String view, String username,
                                             Integer minPrice, Integer maxPrice, Integer maxDuration,
                                             String level, String sort) {
        List<PremiumCourse> courses = premiumCourseRepository.findAll();
        String normalizedQuery = normalize(query);
        String normalizedLanguage = normalize(language);
        String normalizedView = normalize(view);
        String normalizedLevel = normalize(level);
        Set<String> purchasedSlugs = "owned".equals(normalizedView) ? findPurchasedCourseSlugs(username) : Set.of();
        Set<String> wishlistSlugs = "wishlist".equals(normalizedView) ? findWishlistCourseSlugs(username) : Set.of();

        List<PremiumCourse> filteredCourses = courses.stream()
                .filter(course -> normalizedLanguage == null
                        || normalizedLanguage.equals(normalize(course.getLanguage())))
                .filter(course -> normalizedLevel == null
                        || normalizedLevel.equals(normalize(course.getLevel())))
                .filter(course -> minPrice == null || course.getPrice() >= minPrice)
                .filter(course -> maxPrice == null || course.getPrice() <= maxPrice)
                .filter(course -> maxDuration == null || durationHours(course.getDuration()) <= maxDuration)
                .filter(course -> !"owned".equals(normalizedView) || purchasedSlugs.contains(course.getSlug()))
                .filter(course -> !"wishlist".equals(normalizedView) || wishlistSlugs.contains(course.getSlug()))
                .filter(course -> normalizedQuery == null
                        || course.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || course.getSubtitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || course.getLevel().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || containsNormalized(course.getLanguage(), normalizedQuery)
                        || containsNormalized(course.getInstructorName(), normalizedQuery))
                .toList();

        return sortCourses(filteredCourses, sort);
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

    @Transactional(readOnly = true)
    public List<String> findLevels() {
        return premiumCourseRepository.findAll().stream()
                .map(PremiumCourse::getLevel)
                .filter(level -> level != null && !level.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<CourseAuthor> findAuthorBySlug(String slug) {
        return courseAuthorRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public List<PremiumCourse> findCoursesByAuthor(CourseAuthor author) {
        return premiumCourseRepository.findByAuthorOrderByTitleAsc(author);
    }

    @Transactional(readOnly = true)
    public Page<PremiumCourse> findCoursesByAuthor(CourseAuthor author, int page, int size) {
        return page(premiumCourseRepository.findByAuthorOrderByTitleAsc(author), page, size);
    }

    private void backfillSeedMetadata(PremiumCourse existingCourse, PremiumCourse seedCourse) {
        if (existingCourse.getLanguage() == null || existingCourse.getLanguage().isBlank()) {
            existingCourse.setLanguage(seedCourse.getLanguage());
        }
        if (existingCourse.getAuthor() == null) {
            existingCourse.setAuthor(seedCourse.getAuthor());
        }
        if (existingCourse.getInstructor() == null || existingCourse.getInstructor().isBlank()) {
            existingCourse.setInstructor(seedCourse.getInstructor());
        }

        premiumCourseRepository.save(existingCourse);
    }

    private CourseAuthor saveAuthor(CourseAuthor author) {
        return courseAuthorRepository.findBySlug(author.getSlug())
                .map(existingAuthor -> backfillAuthor(existingAuthor, author))
                .orElseGet(() -> courseAuthorRepository.save(author));
    }

    private CourseAuthor backfillAuthor(CourseAuthor existingAuthor, CourseAuthor seedAuthor) {
        existingAuthor.setName(seedAuthor.getName());
        existingAuthor.setHeadline(seedAuthor.getHeadline());
        existingAuthor.setBio(seedAuthor.getBio());
        existingAuthor.setAvatarUrl(seedAuthor.getAvatarUrl());
        existingAuthor.setLocation(seedAuthor.getLocation());
        existingAuthor.setExperience(seedAuthor.getExperience());
        existingAuthor.setRating(seedAuthor.getRating());
        existingAuthor.setStudents(seedAuthor.getStudents());
        existingAuthor.setSpecialties(seedAuthor.getSpecialties());
        existingAuthor.setLinkedinUrl(seedAuthor.getLinkedinUrl());
        existingAuthor.setGithubUrl(seedAuthor.getGithubUrl());
        existingAuthor.setWebsiteUrl(seedAuthor.getWebsiteUrl());
        return courseAuthorRepository.save(existingAuthor);
    }

    private PremiumCourse course(String slug, String title, String subtitle, String level, String language,
                                 String duration, CourseAuthor author, int lessons, int price, String accentColor,
                                 List<String> outcomes, List<String> modules) {
        PremiumCourse course = new PremiumCourse(slug, title, subtitle, level, language, duration, author.getName(),
                lessons, price, accentColor, outcomes, modules);
        course.setAuthor(author);
        return course;
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

    private Page<PremiumCourse> page(List<PremiumCourse> courses, int page, int size) {
        int normalizedSize = Math.max(1, Math.min(size, 24));
        int normalizedPage = Math.max(0, page);
        int start = Math.min(normalizedPage * normalizedSize, courses.size());
        int end = Math.min(start + normalizedSize, courses.size());
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize);
        return new PageImpl<>(courses.subList(start, end), pageable, courses.size());
    }

    private List<PremiumCourse> sortCourses(List<PremiumCourse> courses, String sort) {
        Comparator<PremiumCourse> comparator = switch (normalize(sort) == null ? "" : normalize(sort)) {
            case "price-asc" -> Comparator.comparingInt(PremiumCourse::getPrice);
            case "price-desc" -> Comparator.comparingInt(PremiumCourse::getPrice).reversed();
            case "duration-asc" -> Comparator.comparingInt(course -> durationHours(course.getDuration()));
            case "duration-desc" -> Comparator.comparingInt((PremiumCourse course) -> durationHours(course.getDuration())).reversed();
            default -> Comparator.comparing(PremiumCourse::getTitle, String.CASE_INSENSITIVE_ORDER);
        };

        return courses.stream().sorted(comparator).toList();
    }

    private int durationHours(String duration) {
        if (duration == null || duration.isBlank()) {
            return 0;
        }

        String digits = duration.replaceAll("[^0-9]", "");
        if (digits.isBlank()) {
            return 0;
        }

        return Integer.parseInt(digits);
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
