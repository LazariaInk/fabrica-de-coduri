package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.service.PremiumCourseService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
public class PremiumCourseController {
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
                          Authentication authentication) {
        addSharedAttributes(model);
        model.addAttribute("courses", premiumCourseService.findAll(query));
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("purchasedCourses", purchasedCourseSlugs(authentication));
        return "courses";
    }

    @GetMapping("/courses/{slug}")
    public String courseDetails(@PathVariable String slug,
                                Model model,
                                Authentication authentication) {
        PremiumCourse course = premiumCourseService.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        addSharedAttributes(model);
        model.addAttribute("course", course);
        model.addAttribute("purchased", isLoggedIn(authentication)
                && premiumCourseService.hasPurchasedCourse(authentication.getName(), slug));
        model.addAttribute("loggedIn", isLoggedIn(authentication));
        return "course-details";
    }

    @PostMapping("/courses/{slug}/buy")
    public String buyCourse(@PathVariable String slug,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        PremiumCourse course = premiumCourseService.purchaseCourse(authentication.getName(), slug);
        redirectAttributes.addFlashAttribute("successMessage",
                "Cursul \"" + course.getTitle() + "\" este acum in biblioteca ta.");
        return "redirect:/courses/" + slug + "/watch";
    }

    @GetMapping("/courses/{slug}/watch")
    public String watchCourse(@PathVariable String slug,
                              Model model,
                              Authentication authentication) {
        PremiumCourse course = premiumCourseService.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!premiumCourseService.hasPurchasedCourse(authentication.getName(), slug)) {
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

    private Set<String> purchasedCourseSlugs(Authentication authentication) {
        if (!isLoggedIn(authentication)) {
            return Set.of();
        }

        return premiumCourseService.findPurchasedCourseSlugs(authentication.getName());
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
