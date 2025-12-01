package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.*;
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
            Model model,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        model.addAttribute("topics", topicRepo.findAll());
        PlatformInfo platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);

        Sort springSort = switch (sort == null ? "" : sort) {
            case "priceAsc"  -> Sort.by(Sort.Direction.ASC,  "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            case "new"       -> Sort.by(Sort.Direction.DESC, "id");
            default          -> Sort.by(Sort.Direction.DESC, "id");
        };

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), springSort);

        var spec = CourseSpecifications.build(q, difficulty, priceMin, priceMax);
        Page<Course> pageObj = courseRepository.findAll(spec, pageable);

        int current = pageObj.getNumber();
        int total   = pageObj.getTotalPages();

        int window = 2;
        int start = Math.max(0, current - window);
        int end   = Math.min(total - 1, current + window);

        if (current - start < window) end = Math.min(total - 1, start + window*2);
        if (end - current < window)   start = Math.max(0, end - window*2);

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

        // 👇 nou: cursuri deținute de userul logat
        java.util.Set<Long> ownedCourseIds = new java.util.HashSet<>();

        if (principal != null) {
            var user = userRepo.findByEmail(principal.getUsername())
                    .orElseGet(() -> userRepo.findByUsername(principal.getUsername()).orElse(null));

            if (user != null) {
                var ownerships = ownershipRepo.findByUserIdAndStatus(
                        user.getId(), CourseOwnership.Status.PAID);

                for (var own : ownerships) {
                    if (own.getCourse() != null && own.getCourse().getId() != null) {
                        ownedCourseIds.add(own.getCourse().getId());
                    }
                }
            }
        }

        model.addAttribute("ownedCourseIds", ownedCourseIds);

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

    @GetMapping("/{slug}/continua")
    public String watchCourse(@PathVariable String slug,
                              @RequestParam(required = false) Long lessonId,
                              Model model,
                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        if (principal == null) {
            // dacă cineva intră direct pe URL fără login
            return "redirect:/login";
        }

        var user = userRepo.findByEmail(principal.getUsername())
                .orElseGet(() -> userRepo.findByUsername(principal.getUsername())
                        .orElse(null));

        if (user == null) {
            return "redirect:/login";
        }

        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND));

        boolean hasCourse = ownershipRepo.existsByUserIdAndCourseIdAndStatus(
                user.getId(), course.getId(), CourseOwnership.Status.PAID);

        if (!hasCourse) {
            return "redirect:/cursuri/" + slug;
        }

        java.util.List<CourseLesson> allLessons = new java.util.ArrayList<>();
        course.getChapters().stream()
                .sorted(java.util.Comparator.comparing(CourseChapter::getPosition))
                .forEach(ch -> ch.getLessons().stream()
                        .sorted(java.util.Comparator.comparing(CourseLesson::getPosition))
                        .forEach(allLessons::add));

        if (allLessons.isEmpty()) {
            model.addAttribute("user", user);
            model.addAttribute("course", course);
            model.addAttribute("currentLesson", null);
            model.addAttribute("hasCourse", hasCourse);
            model.addAttribute("topics", topicRepo.findAll());
            var platformInfo = platformInfoRepository.findById(1L).orElse(null);
            model.addAttribute("platformInfo", platformInfo);
            return "course-watch";
        }

        CourseLesson currentLesson = null;
        if (lessonId != null) {
            for (CourseLesson ls : allLessons) {
                if (lessonId.equals(ls.getId())) {
                    currentLesson = ls;
                    break;
                }
            }
        }
        if (currentLesson == null) {
            currentLesson = allLessons.get(0);
        }

        CourseLesson prevLesson = null;
        CourseLesson nextLesson = null;
        for (int i = 0; i < allLessons.size(); i++) {
            if (allLessons.get(i).getId().equals(currentLesson.getId())) {
                if (i > 0) prevLesson = allLessons.get(i - 1);
                if (i < allLessons.size() - 1) nextLesson = allLessons.get(i + 1);
                break;
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("currentLesson", currentLesson);
        model.addAttribute("prevLesson", prevLesson);
        model.addAttribute("nextLesson", nextLesson);
        model.addAttribute("hasCourse", hasCourse);
        model.addAttribute("allLessons", allLessons);
        model.addAttribute("topics", topicRepo.findAll());
        var platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);

        return "course-watch";
    }

}
