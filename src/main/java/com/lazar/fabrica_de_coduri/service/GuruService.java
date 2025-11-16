package com.lazar.fabrica_de_coduri.service;


import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.CourseOwnership;
import com.lazar.fabrica_de_coduri.model.CourseOwnership.Status;
import com.lazar.fabrica_de_coduri.model.GuruLeaderboardEntry;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.CourseOwnershipRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GuruService {

    private final CourseOwnershipRepository courseOwnershipRepository;

    private static final Set<String> FRONTEND_TECHS = Set.of(
            "html", "css", "javascript", "js", "typescript", "react", "angular",
            "vue", "svelte", "next.js", "nuxt", "tailwind", "bootstrap"
    );

    private static final Set<String> BACKEND_TECHS = Set.of(
            "java", "spring", "spring boot", "node.js", "node", "express",
            "python", "django", "flask", "fastapi", "php", "laravel",
            "c#", ".net", "dotnet", "ruby", "rails", "go", "golang",
            "postgresql", "mysql", "mongodb", "redis"
    );

    public GuruService(CourseOwnershipRepository courseOwnershipRepository) {
        this.courseOwnershipRepository = courseOwnershipRepository;
    }

    public List<GuruLeaderboardEntry> getTopGurus(int limit) {

        List<CourseOwnership> all = courseOwnershipRepository.findByStatus(Status.PAID);

        Map<User, List<CourseOwnership>> byUser = all.stream()
                .filter(co -> co.getUser() != null && co.getCourse() != null)
                .collect(Collectors.groupingBy(CourseOwnership::getUser));

        List<GuruLeaderboardEntry> entries = new ArrayList<>();

        for (Map.Entry<User, List<CourseOwnership>> entry : byUser.entrySet()) {
            User user = entry.getKey();
            List<CourseOwnership> userOwnerships = entry.getValue();

            if (userOwnerships.isEmpty()) continue;

            GuruLeaderboardEntry guru = buildGuruEntry(user, userOwnerships);
            entries.add(guru);
        }

        return entries.stream()
                .sorted(Comparator.comparingInt(GuruLeaderboardEntry::getGuruScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private GuruLeaderboardEntry buildGuruEntry(User user, List<CourseOwnership> ownerships) {
        GuruLeaderboardEntry dto = new GuruLeaderboardEntry();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());

        int coursesCount = ownerships.size();
        int completedCourses = (int) ownerships.stream()
                .filter(o -> o.getProgress() != null && o.getProgress() >= 90) // aproximăm "terminat"
                .count();

        dto.setCoursesCount(coursesCount);
        dto.setCompletedCourses(completedCourses);

        Map<String, Long> techFrequency = ownerships.stream()
                .map(CourseOwnership::getCourse)
                .filter(Objects::nonNull)
                .map(Course::getTechnology)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.groupingBy(
                        t -> t.toLowerCase(Locale.ROOT),
                        Collectors.counting()
                ));

        int frontendScore = 0;
        int backendScore = 0;

        for (String techLower : techFrequency.keySet()) {
            if (isFrontendTech(techLower)) {
                frontendScore++;
            }
            if (isBackendTech(techLower)) {
                backendScore++;
            }
        }

        dto.setFrontendScore(frontendScore);
        dto.setBackendScore(backendScore);
        dto.setTechCount(techFrequency.size());

        String topTechs = techFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(4)
                .map(e -> capitalizeTech(e.getKey()))
                .collect(Collectors.joining(", "));

        dto.setTopTechnologies(topTechs);

        String mainStack;
        if (frontendScore > backendScore) {
            mainStack = "Front-end";
        } else if (backendScore > frontendScore) {
            mainStack = "Back-end";
        } else if (frontendScore == 0 && backendScore == 0) {
            mainStack = "Explorer";
        } else {
            mainStack = "Full-stack";
        }
        dto.setMainStack(mainStack);

        int guruScore =
                dto.getTechCount() * 15 +
                        completedCourses * 10 +
                        coursesCount * 5 +
                        frontendScore * 5 +
                        backendScore * 5;

        dto.setGuruScore(guruScore);

        return dto;
    }

    private boolean isFrontendTech(String techLower) {
        return FRONTEND_TECHS.stream().anyMatch(techLower::contains);
    }

    private boolean isBackendTech(String techLower) {
        return BACKEND_TECHS.stream().anyMatch(techLower::contains);
    }

    private String capitalizeTech(String techLower) {
        if (techLower.equals("js")) return "JS";
        if (techLower.equals("c#")) return "C#";
        if (techLower.equals("go")) return "Go";
        if (techLower.equals(".net") || techLower.equals("dotnet")) return ".NET";

        if (techLower.length() <= 2) {
            return techLower.toUpperCase(Locale.ROOT);
        }
        return techLower.substring(0, 1).toUpperCase(Locale.ROOT) + techLower.substring(1);
    }
}
