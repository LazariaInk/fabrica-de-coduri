package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cursuri")
public class CourseController {
    @Autowired
    private TopicRepository topicRepo;
    @Autowired
    private PlatformInfoRepository platformInfoRepository;
    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public String list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "list") String view,
            Model model) {


        model.addAttribute("topics", topicRepo.findAll());
        PlatformInfo platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);
        var all = courseRepository.findAllByOrderByIdDesc();

        var filtered = all.stream()
                .filter(c -> q == null || q.isBlank() ||
                        containsIgnoreCase(c.getTitle(), q) ||
                        containsIgnoreCase(c.getShortDescription(), q) ||
                        containsIgnoreCase(c.getAuthor(), q) ||
                        containsIgnoreCase(c.getTechnology(), q) ||
                        containsIgnoreCase(c.getHashtags(), q))
                .filter(c -> difficulty == null || difficulty.isBlank() ||
                        c.getLevel().name().equalsIgnoreCase(difficulty))
                .filter(c -> priceMin == null || c.getPrice().compareTo(priceMin) >= 0)
                .filter(c -> priceMax == null || c.getPrice().compareTo(priceMax) <= 0)
                .collect(Collectors.toList());

        Comparator<Course> cmp = Comparator.comparing(Course::getId).reversed(); // implicit
        if ("priceAsc".equalsIgnoreCase(sort)) cmp = Comparator.comparing(Course::getPrice);
        else if ("priceDesc".equalsIgnoreCase(sort)) cmp = Comparator.comparing(Course::getPrice).reversed();
        else if ("new".equalsIgnoreCase(sort)) cmp = Comparator.comparing(Course::getId).reversed();
        filtered.sort(cmp);

        model.addAttribute("courses", filtered);

        Map<String, Object> params = new HashMap<>();
        params.put("q", q); params.put("difficulty", difficulty);
        params.put("priceMin", priceMin); params.put("priceMax", priceMax);
        params.put("sort", sort); params.put("view", view);
        model.addAttribute("params", params);


        return "all-courses";
    }

    private boolean containsIgnoreCase(String hay, String needle) {
        return hay != null && needle != null && hay.toLowerCase().contains(needle.toLowerCase());
    }
    private boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    @GetMapping("/{slug}")
    public String details(@PathVariable String slug, Model model) {
        var course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        model.addAttribute("course", course);
        return "course-details";
    }
}
