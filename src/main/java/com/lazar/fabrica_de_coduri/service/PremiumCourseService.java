package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.PremiumCourseRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PremiumCourseService {
    private final PremiumCourseRepository premiumCourseRepository;
    private final UserRepository userRepository;

    public PremiumCourseService(PremiumCourseRepository premiumCourseRepository, UserRepository userRepository) {
        this.premiumCourseRepository = premiumCourseRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void seedCourses() {
        if (premiumCourseRepository.count() > 0) {
            return;
        }

        premiumCourseRepository.saveAll(List.of(
                new PremiumCourse(
                        "java-spring-boot-complet",
                        "Java Spring Boot Complet",
                        "Construieste aplicatii web reale cu Spring Boot, Security, JPA si Thymeleaf.",
                        "Intermediar",
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
                        "15 ore",
                        "Fabrica de Coduri",
                        72,
                        249,
                        "#f97316",
                        List.of("React modern", "State management", "Consum API", "UI responsive si profesional"),
                        List.of("Fundamente React", "Componente si hooks", "Aplicatie completa", "Optimizare")
                )
        ));
    }

    @Transactional(readOnly = true)
    public List<PremiumCourse> findAll(String query) {
        List<PremiumCourse> courses = premiumCourseRepository.findAll();
        if (query == null || query.isBlank()) {
            return courses;
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT).trim();
        return courses.stream()
                .filter(course -> course.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || course.getSubtitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || course.getLevel().toLowerCase(Locale.ROOT).contains(normalizedQuery))
                .toList();
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
    public boolean hasPurchasedCourse(String username, String slug) {
        return findPurchasedCourseSlugs(username).contains(slug);
    }

    @Transactional
    public PremiumCourse purchaseCourse(String username, String slug) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PremiumCourse course = premiumCourseRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        user.getPurchasedCourses().add(course);
        userRepository.save(user);
        return course;
    }
}
