package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.CourseComment;
import com.lazar.fabrica_de_coduri.model.CourseOwnership;
import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cursuri")
public class CourseController {

    @Autowired
    private TopicRepository topicRepo;
    @Autowired
    private PlatformInfoRepository platformInfoRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseCommentRepository commentRepository;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CourseOwnershipRepository ownershipRepo;

    @GetMapping
    public String list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            Model model) {

        model.addAttribute("topics", topicRepo.findAll());
        PlatformInfo platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);

        // sort mapping
        Sort springSort = switch (sort == null ? "" : sort) {
            case "priceAsc"  -> Sort.by(Sort.Direction.ASC,  "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            case "new"       -> Sort.by(Sort.Direction.DESC, "id");
            default          -> Sort.by(Sort.Direction.DESC, "id"); // implicit: cele mai noi
        };

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), springSort);

        var spec = CourseSpecifications.build(q, difficulty, priceMin, priceMax);
        Page<Course> pageObj = courseRepository.findAll(spec, pageable);


        int current = pageObj.getNumber();
        int total   = pageObj.getTotalPages();

        int window = 2;
        int start = Math.max(0, current - window);
        int end   = Math.min(total - 1, current + window);

        if (current - start < window) {
            end = Math.min(total - 1, start + window*2);
        }
        if (end - current < window) {
            start = Math.max(0, end - window*2);
        }

        List<Integer> pageNumbers = new java.util.ArrayList<>();
        for (int i = start; i <= end; i++) pageNumbers.add(i);

        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("totalPages", total);
        model.addAttribute("currentPage", current);

        model.addAttribute("courses", pageObj.getContent());
        model.addAttribute("page", pageObj);
        Map<String, Object> params = new HashMap<>();
        params.put("q", q);
        params.put("difficulty", difficulty);
        params.put("priceMin", priceMin);
        params.put("priceMax", priceMax);
        params.put("sort", sort);
        params.put("size", size);
        model.addAttribute("params", params);

        return "all-courses";
    }

    @GetMapping("/{slug}")
    public String details(@PathVariable String slug,
                          Model model,
                          @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        model.addAttribute("topics", topicRepo.findAll());
        var platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);

        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND));

        model.addAttribute("commentsOrdered", commentRepository.findAllForCourseOrdered(course.getId()));

        boolean hasCourse = false;
        CourseComment myComment = null;

        if (principal != null) {
            var user = userRepo.findByEmail(principal.getUsername())
                    .orElseGet(() -> userRepo.findByUsername(principal.getUsername())
                            .orElse(null));

            if (user != null) {
                hasCourse = ownershipRepo.existsByUserIdAndCourseIdAndStatus(
                        user.getId(), course.getId(), CourseOwnership.Status.PAID);

                myComment = commentRepository
                        .findFirstByCourseIdAndUserIdOrderByCreatedAtDesc(course.getId(), user.getId())
                        .orElse(null);
            }
        }

        model.addAttribute("hasCourse", hasCourse);
        model.addAttribute("myComment", myComment);
        model.addAttribute("course", course);
        return "course-details";
    }

}
