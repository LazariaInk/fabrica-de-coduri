package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.dto.CoursesPageDTO;
import com.lazar.fabrica_de_coduri.service.CourseQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final CourseQueryService courseQueryService;

    public DashboardController(CourseQueryService courseQueryService) {
        this.courseQueryService = courseQueryService;
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("canonicalUrl", "https://fabricadecoduri.ro/");
        return "courses";
    }

    @GetMapping("/courses/data")
    @ResponseBody
    public CoursesPageDTO coursesData(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "stack", required = false) String stack,
            @RequestParam(value = "lang", required = false) String lang,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "9") int size
    ) {
        return courseQueryService.search(q, stack, lang, page, size);
    }
}
