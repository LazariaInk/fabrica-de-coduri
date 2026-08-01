package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import com.lazar.fabrica_de_coduri.model.CourseVideo;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.service.PremiumCourseService;
import com.lazar.fabrica_de_coduri.service.VideoStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class PremiumCourseController {
    // Temporar dezactivat pentru deploy: cursurile video premium nu sunt disponibile public.
    private static final boolean VIDEO_COURSES_ENABLED = false;

    private final PremiumCourseService premiumCourseService;
    private final VideoStorageService videoStorageService;
    private final TopicRepository topicRepository;
    private final PlatformInfoRepository platformInfoRepository;

    public PremiumCourseController(PremiumCourseService premiumCourseService,
                                   VideoStorageService videoStorageService,
                                   TopicRepository topicRepository,
                                   PlatformInfoRepository platformInfoRepository) {
        this.premiumCourseService = premiumCourseService;
        this.videoStorageService = videoStorageService;
        this.topicRepository = topicRepository;
        this.platformInfoRepository = platformInfoRepository;
    }

    @GetMapping("/courses")
    public String courses(@RequestParam(value = "q", required = false) String query,
                          @RequestParam(value = "language", required = false) String language,
                          @RequestParam(value = "level", required = false) String level,
                          @RequestParam(value = "minPrice", required = false) Integer minPrice,
                          @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
                          @RequestParam(value = "maxDuration", required = false) Integer maxDuration,
                          @RequestParam(value = "sort", required = false, defaultValue = "title") String sort,
                          @RequestParam(value = "view", required = false, defaultValue = "all") String view,
                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                          @RequestParam(value = "size", required = false, defaultValue = "6") int size,
                          Model model,
                          Authentication authentication) {
        if (!VIDEO_COURSES_ENABLED) {
            return "redirect:/";
        }

        if (!isLoggedIn(authentication) && ("owned".equals(view) || "wishlist".equals(view))) {
            return "redirect:/login";
        }

        addSharedAttributes(model);
        Page<PremiumCourse> coursePage = premiumCourseService.findPage(query, language, view, username(authentication),
                minPrice, maxPrice, maxDuration, level, sort, page, size);
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("languages", premiumCourseService.findLanguages());
        model.addAttribute("levels", premiumCourseService.findLevels());
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("selectedLanguage", language == null ? "" : language);
        model.addAttribute("selectedLevel", level == null ? "" : level);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("maxDuration", maxDuration);
        model.addAttribute("selectedSort", sort == null ? "title" : sort);
        model.addAttribute("selectedView", view == null ? "all" : view);
        model.addAttribute("pageSize", size);
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
        if (!VIDEO_COURSES_ENABLED) {
            return "redirect:/";
        }

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
        if (!VIDEO_COURSES_ENABLED) {
            return "redirect:/";
        }

        PremiumCourse course = premiumCourseService.purchaseCourse(authentication.getName(), slug);
        redirectAttributes.addFlashAttribute("successMessage",
                "Cursul \"" + course.getTitle() + "\" este acum in biblioteca ta.");
        return "redirect:/courses/" + slug + "/watch";
    }

    @PostMapping("/courses/{slug}/wishlist")
    public String toggleWishlist(@PathVariable String slug,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (!VIDEO_COURSES_ENABLED) {
            return "redirect:/";
        }

        boolean added = premiumCourseService.toggleWishlistCourse(authentication.getName(), slug);
        redirectAttributes.addFlashAttribute("successMessage",
                added ? "Cursul a fost adaugat in wishlist." : "Cursul a fost scos din wishlist.");
        return "redirect:/courses/" + slug;
    }

    @GetMapping("/courses/{slug}/watch")
    public String watchCourse(@PathVariable String slug,
                              Model model,
                              Authentication authentication) {
        if (!VIDEO_COURSES_ENABLED) {
            return "redirect:/";
        }

        PremiumCourse course = premiumCourseService.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!premiumCourseService.hasPurchasedCourse(authentication.getName(), slug)) {
            return "redirect:/courses/" + slug;
        }

        addSharedAttributes(model);
        List<CourseVideo> videos = premiumCourseService.findVideos(slug);
        model.addAttribute("course", course);
        model.addAttribute("videos", videos);
        model.addAttribute("currentVideo", videos.isEmpty() ? null : videos.get(0));
        model.addAttribute("videoProgress", premiumCourseService.findVideoProgress(authentication.getName(), slug));
        model.addAttribute("completedLessons", premiumCourseService.findCompletedLessons(authentication.getName(), slug));
        model.addAttribute("progressPercent", premiumCourseService.findProgressPercent(authentication.getName(), slug));
        model.addAttribute("courseComments", premiumCourseService.findComments(slug));
        model.addAttribute("myCourseComment", premiumCourseService.findCommentForUser(authentication.getName(), slug).orElse(null));
        return "course-watch";
    }

    @GetMapping("/courses/{slug}/videos/{videoId}/stream")
    @ResponseBody
    public ResponseEntity<ResourceRegion> streamVideo(@PathVariable String slug,
                                                      @PathVariable Long videoId,
                                                      @RequestHeader HttpHeaders headers,
                                                      Authentication authentication) throws IOException {
        if (!VIDEO_COURSES_ENABLED) {
            return ResponseEntity.notFound().build();
        }

        CourseVideo video = premiumCourseService.findPurchasedVideo(authentication.getName(), slug, videoId).orElse(null);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource;
        try {
            resource = videoStorageService.load(video.getStorageKey());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }

        long contentLength = resource.contentLength();
        ResourceRegion region = resourceRegion(resource, headers.getRange(), contentLength);

        return ResponseEntity.status(headers.getRange().isEmpty() ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    @PostMapping("/courses/{slug}/videos/{videoId}/progress")
    @ResponseBody
    public Map<String, Integer> saveVideoProgress(@PathVariable String slug,
                                                  @PathVariable Long videoId,
                                                  @RequestParam("watchedSeconds") int watchedSeconds,
                                                  @RequestParam(value = "durationSeconds", required = false) Integer durationSeconds,
                                                  Authentication authentication) {
        if (!VIDEO_COURSES_ENABLED) {
            return Map.of("progressPercent", 0, "completedLessons", 0);
        }

        PremiumCourseService.VideoProgressResult progress = premiumCourseService.saveVideoProgress(authentication.getName(), slug, videoId,
                watchedSeconds, durationSeconds);
        return Map.of(
                "progressPercent", progress.progressPercent(),
                "completedLessons", progress.completedLessons()
        );
    }

    @PostMapping("/courses/{slug}/progress")
    public String completeNextLesson(@PathVariable String slug,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        if (!VIDEO_COURSES_ENABLED) {
            return "redirect:/";
        }

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
        if (!VIDEO_COURSES_ENABLED) {
            return "redirect:/";
        }

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

    private ResourceRegion resourceRegion(Resource video, List<HttpRange> ranges, long contentLength) {
        if (ranges == null || ranges.isEmpty()) {
            return new ResourceRegion(video, 0, contentLength);
        }

        HttpRange range = ranges.get(0);
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long rangeLength = Math.min(1024 * 1024, end - start + 1);
        return new ResourceRegion(video, start, rangeLength);
    }
}
