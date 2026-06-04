package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.service.PremiumCourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

@Controller
public class PremiumCourseController {
    private static final String PURCHASED_COURSES_KEY = "purchasedCourses";

    private final PremiumCourseService premiumCourseService;
    private final TopicRepository topicRepository;
    private final PlatformInfoRepository platformInfoRepository;

    public PremiumCourseController(PremiumCourseService premiumCourseService,
                                   TopicRepository topicRepository,
                                   PlatformInfoRepository platformInfoRepository) {
        this.premiumCourseService = premiumCourseService;
        this.topicRepository = topicRepository;
        this.platformInfoRepository = platformInfoRepository;
    }

    @GetMapping("/courses")
    public String courses(@RequestParam(value = "q", required = false) String query,
                          Model model,
                          HttpSession session) {
        addSharedAttributes(model);
        model.addAttribute("courses", premiumCourseService.findAll(query));
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("purchasedCourses", purchasedCourses(session));
        return "courses";
    }

    @GetMapping("/courses/{slug}")
    public String courseDetails(@PathVariable String slug,
                                Model model,
                                HttpSession session,
                                Authentication authentication) {
        PremiumCourse course = premiumCourseService.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        addSharedAttributes(model);
        model.addAttribute("course", course);
        model.addAttribute("purchased", purchasedCourses(session).contains(slug));
        model.addAttribute("loggedIn", authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken));
        return "course-details";
    }

    @PostMapping("/courses/{slug}/buy")
    public String buyCourse(@PathVariable String slug,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        PremiumCourse course = premiumCourseService.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        purchasedCourses(session).add(slug);
        redirectAttributes.addFlashAttribute("successMessage",
                "Cursul \"" + course.getTitle() + "\" este acum in biblioteca ta.");
        return "redirect:/courses/" + slug + "/watch";
    }

    @GetMapping("/courses/{slug}/watch")
    public String watchCourse(@PathVariable String slug,
                              Model model,
                              HttpSession session) {
        PremiumCourse course = premiumCourseService.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!purchasedCourses(session).contains(slug)) {
            return "redirect:/courses/" + slug;
        }

        addSharedAttributes(model);
        model.addAttribute("course", course);
        return "course-watch";
    }

    private void addSharedAttributes(Model model) {
        model.addAttribute("topics", topicRepository.findAll());
        model.addAttribute("platformInfo", platformInfoRepository.findById(1L).orElse(null));
    }

    @SuppressWarnings("unchecked")
    private Set<String> purchasedCourses(HttpSession session) {
        Set<String> purchasedCourses = (Set<String>) session.getAttribute(PURCHASED_COURSES_KEY);
        if (purchasedCourses == null) {
            purchasedCourses = new HashSet<>();
            session.setAttribute(PURCHASED_COURSES_KEY, purchasedCourses);
        }
        return purchasedCourses;
    }
}
