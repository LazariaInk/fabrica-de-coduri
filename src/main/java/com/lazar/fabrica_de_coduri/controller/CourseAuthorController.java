package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.CourseAuthor;
import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.service.PremiumCourseService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CourseAuthorController {
    private final PremiumCourseService premiumCourseService;
    private final TopicRepository topicRepository;
    private final PlatformInfoRepository platformInfoRepository;

    public CourseAuthorController(PremiumCourseService premiumCourseService,
                                  TopicRepository topicRepository,
                                  PlatformInfoRepository platformInfoRepository) {
        this.premiumCourseService = premiumCourseService;
        this.topicRepository = topicRepository;
        this.platformInfoRepository = platformInfoRepository;
    }

    @GetMapping("/authors/{slug}")
    public String authorProfile(@PathVariable String slug,
                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                @RequestParam(value = "size", required = false, defaultValue = "4") int size,
                                Model model) {
        CourseAuthor author = premiumCourseService.findAuthorBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        Page<PremiumCourse> coursePage = premiumCourseService.findCoursesByAuthor(author, page, size);

        model.addAttribute("topics", topicRepository.findAll());
        model.addAttribute("platformInfo", platformInfoRepository.findById(1L).orElse(null));
        model.addAttribute("author", author);
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("pageSize", size);
        return "author-profile";
    }
}
