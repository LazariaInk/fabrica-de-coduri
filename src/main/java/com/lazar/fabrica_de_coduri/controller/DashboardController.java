package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.*;
import com.lazar.fabrica_de_coduri.repository.CourseOwnershipRepository;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
@PreAuthorize("isAuthenticated()")
public class DashboardController {

    private final UserRepository userRepo;
    private final CourseOwnershipRepository ownershipRepo;
    private final TopicRepository topicRepo;
    private final PlatformInfoRepository platformInfoRepository;

    public DashboardController(UserRepository userRepo,
                               CourseOwnershipRepository ownershipRepo,
                               TopicRepository topicRepo,
                               PlatformInfoRepository platformInfoRepository) {
        this.topicRepo = topicRepo;
        this.platformInfoRepository = platformInfoRepository;
        this.userRepo = userRepo;
        this.ownershipRepo = ownershipRepo;
    }

    @GetMapping
    public String dashboard(Model model,
                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
                            @RequestParam(required = false) String q) {

        User user = userRepo.findByEmail(principal.getUsername())
                .orElseGet(() -> userRepo.findByUsername(principal.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found")));

        // ownership-urile reale din DB (dacă ai)
        List<CourseOwnership> owns = ownershipRepo
                .findByUserIdAndStatusOrderByPurchasedAtDesc(
                        user.getId(),
                        CourseOwnership.Status.PAID
                );

        // listă de Course doar pentru filtrare după q
        List<Course> myCourses = new ArrayList<>();
        for (CourseOwnership own : owns) {
            if (own.getCourse() != null) {
                myCourses.add(own.getCourse());
            }
        }

        if (q != null && !q.isBlank()) {
            String qq = q.toLowerCase();
            myCourses = myCourses.stream()
                    .filter(c ->
                            (c.getTitle() != null && c.getTitle().toLowerCase().contains(qq)) ||
                                    (c.getTechnology() != null && c.getTechnology().toLowerCase().contains(qq)) ||
                                    (c.getHashtags() != null && c.getHashtags().toLowerCase().contains(qq)) ||
                                    (c.getShortDescription() != null && c.getShortDescription().toLowerCase().contains(qq))
                    )
                    .toList();

            // dacă vrei ca filtrul să se aplice și pe ownership (nu doar pe courses),
            // poți filtra owns după id-urile cursurilor de mai sus.
            // Deocamdată lăsăm simplu.
        }

        model.addAttribute("user", user);
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("topics", topicRepo.findAll());

        // în template, myCourses este de fapt List<CourseOwnership>, nu List<Course>
        model.addAttribute("myCourses", owns);

        PlatformInfo platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);

        // 👇 Mock data pentru leaderboard „Gurii comunității”
        // deocamdată punem direct mock, fără DB.
        List<GuruLeaderboardEntry> topGurus = createMockGurus();
        model.addAttribute("topGurus", topGurus);

        return "dashboard";
    }

    /**
     * Mock data pentru leaderboard, până ai date reale în DB.
     */
    private List<GuruLeaderboardEntry> createMockGurus() {
        List<GuruLeaderboardEntry> list = new ArrayList<>();

        // #1 – Front-end heavy
        GuruLeaderboardEntry g1 = new GuruLeaderboardEntry();
        g1.setUserId(1L);
        g1.setUsername("frontend_wizard");
        g1.setMainStack("Front-end");
        g1.setFrontendScore(6);
        g1.setBackendScore(1);
        g1.setTechCount(7);
        g1.setTopTechnologies("React, TypeScript, Next.js, Tailwind");
        g1.setCoursesCount(8);
        g1.setCompletedCourses(6);
        g1.setGuruScore(1650);
        g1.setInstagramLink("https://www.instagram.com/fabricadecoduri/");

        // #2 – Full-stack
        GuruLeaderboardEntry g2 = new GuruLeaderboardEntry();
        g2.setUserId(2L);
        g2.setUsername("fullstack_dragon");
        g2.setMainStack("Full-stack");
        g2.setFrontendScore(4);
        g2.setBackendScore(4);
        g2.setTechCount(9);
        g2.setTopTechnologies("Angular, Spring Boot, PostgreSQL, Docker");
        g2.setCoursesCount(10);
        g2.setCompletedCourses(7);
        g2.setGuruScore(1580);
        g2.setInstagramLink("https://www.instagram.com/fabricadecoduri/");

        // #3 – Back-end lover
        GuruLeaderboardEntry g3 = new GuruLeaderboardEntry();
        g3.setUserId(3L);
        g3.setUsername("api_blacksmith");
        g3.setMainStack("Back-end");
        g3.setFrontendScore(1);
        g3.setBackendScore(6);
        g3.setTechCount(8);
        g3.setTopTechnologies("Java, Spring, Redis, Kafka");
        g3.setCoursesCount(7);
        g3.setCompletedCourses(5);
        g3.setGuruScore(1490);
        g3.setInstagramLink("https://www.instagram.com/fabricadecoduri/");

        // #4 – DevOps-ish
        GuruLeaderboardEntry g4 = new GuruLeaderboardEntry();
        g4.setUserId(4L);
        g4.setUsername("cloud_juggler");
        g4.setMainStack("Back-end");
        g4.setFrontendScore(1);
        g4.setBackendScore(5);
        g4.setTechCount(7);
        g4.setTopTechnologies("Node.js, Docker, Kubernetes, AWS");
        g4.setCoursesCount(6);
        g4.setCompletedCourses(4);
        g4.setGuruScore(1380);
        g4.setInstagramLink("https://www.instagram.com/fabricadecoduri/");

        // #5 – Beginner explorer
        GuruLeaderboardEntry g5 = new GuruLeaderboardEntry();
        g5.setUserId(5L);
        g5.setUsername("junior_explorer");
        g5.setMainStack("Explorer");
        g5.setFrontendScore(2);
        g5.setBackendScore(1);
        g5.setTechCount(3);
        g5.setTopTechnologies("HTML, CSS, JavaScript");
        g5.setCoursesCount(3);
        g5.setCompletedCourses(1);
        g5.setGuruScore(940);
        g5.setInstagramLink("https://www.instagram.com/fabricadecoduri/");

        list.add(g1);
        list.add(g2);
        list.add(g3);
        list.add(g4);
        list.add(g5);

        return list;
    }
}
