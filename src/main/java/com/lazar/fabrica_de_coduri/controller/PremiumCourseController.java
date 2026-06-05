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
                          @RequestParam(value = "language", required = false) String language,
                          @RequestParam(value = "view", required = false, defaultValue = "all") String view,
                          Model model,
                          Authentication authentication) {
        if (!isLoggedIn(authentication) && ("owned".equals(view) || "wishlist".equals(view))) {
            return "redirect:/login";
        }

        addSharedAttributes(model);
        model.addAttribute("courses", premiumCourseService.findAll(query, language, view, username(authentication)));
        model.addAttribute("languages", premiumCourseService.findLanguages());
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("selectedLanguage", language == null ? "" : language);
        model.addAttribute("selectedView", view == null ? "all" : view);
        model.addAttribute("purchasedCourses", purchasedCourseSlugs(authentication));
        model.addAttribute("wishlistCourses", wishlistCourseSlugs(authentication));
        model.addAttribute("courseProgress", isLoggedIn(authentication)
                ? premiumCourseService.findProgressPercentByCourseSlug(authentication.getName())
                : java.util.Map.of());
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
        boolean purchased = isLoggedIn(authentication)
                && premiumCourseService.hasPurchasedCourse(authentication.getName(), slug);
        model.addAttribute("purchased", purchased);
        model.addAttribute("wishlisted", isLoggedIn(authentication)
                && premiumCourseService.findWishlistCourseSlugs(authentication.getName()).contains(slug));
        model.addAttribute("progressPercent", isLoggedIn(authentication)
                ? premiumCourseService.findProgressPercent(authentication.getName(), slug)
                : 0);
        model.addAttribute("courseComments", premiumCourseService.findComments(slug));
        model.addAttribute("myCourseComment", purchased
                ? premiumCourseService.findCommentForUser(authentication.getName(), slug).orElse(null)
                : null);
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

    @PostMapping("/courses/{slug}/wishlist")
    public String toggleWishlist(@PathVariable String slug,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        boolean added = premiumCourseService.toggleWishlistCourse(authentication.getName(), slug);
        redirectAttributes.addFlashAttribute("successMessage",
                added ? "Cursul a fost adaugat in wishlist." : "Cursul a fost scos din wishlist.");
        return "redirect:/courses/" + slug;
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
        model.addAttribute("completedLessons", premiumCourseService.findCompletedLessons(authentication.getName(), slug));
        model.addAttribute("progressPercent", premiumCourseService.findProgressPercent(authentication.getName(), slug));
        model.addAttribute("courseComments", premiumCourseService.findComments(slug));
        model.addAttribute("myCourseComment", premiumCourseService.findCommentForUser(authentication.getName(), slug).orElse(null));
        return "course-watch";
    }

    @PostMapping("/courses/{slug}/progress")
    public String completeNextLesson(@PathVariable String slug,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        premiumCourseService.completeNextLesson(authentication.getName(), slug);
        redirectAttributes.addFlashAttribute("successMessage", "Progresul a fost actualizat.");
        return "redirect:/courses/" + slug + "/watch";
    }

    @PostMapping("/courses/{slug}/comments")
    public String saveComment(@PathVariable String slug,
                              @RequestParam("content") String content,
                              @RequestParam(value = "returnTo", required = false, defaultValue = "details") String returnTo,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            premiumCourseService.saveComment(authentication.getName(), slug, content);
            redirectAttributes.addFlashAttribute("successMessage", "Comentariul tau a fost salvat.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Comentariul nu a putut fi salvat. Verifica daca ai cumparat cursul si daca textul nu este gol.");
        }

        if ("watch".equals(returnTo)) {
            return "redirect:/courses/" + slug + "/watch";
        }

        return "redirect:/courses/" + slug;
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

    private Set<String> wishlistCourseSlugs(Authentication authentication) {
        if (!isLoggedIn(authentication)) {
            return Set.of();
        }

        return premiumCourseService.findWishlistCourseSlugs(authentication.getName());
    }

    private String username(Authentication authentication) {
        if (!isLoggedIn(authentication)) {
            return null;
        }

        return authentication.getName();
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
