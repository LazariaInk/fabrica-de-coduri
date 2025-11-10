package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.CourseOwnership;
import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.CourseOwnershipRepository;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        this.topicRepo =topicRepo;
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

        List<CourseOwnership> owns = ownershipRepo
                .findByUserIdAndStatusOrderByPurchasedAtDesc(
                        user.getId(),
                        CourseOwnership.Status.PAID
                );

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
        }

        model.addAttribute("user", user);
        model.addAttribute("myCourses", myCourses);
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("topics", topicRepo.findAll());
        model.addAttribute("myCourses", owns);
        PlatformInfo platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);

        return "dashboard";
    }
}
