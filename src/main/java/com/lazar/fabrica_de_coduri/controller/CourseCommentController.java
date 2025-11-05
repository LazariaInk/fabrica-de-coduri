package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.CourseComment;
import com.lazar.fabrica_de_coduri.model.CourseOwnership;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.CourseCommentRepository;
import com.lazar.fabrica_de_coduri.repository.CourseOwnershipRepository;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/cursuri/{slug}/comments")
public class CourseCommentController {

    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CourseOwnershipRepository ownershipRepo;
    @Autowired
    private CourseCommentRepository commentRepo;
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String create(@PathVariable String slug,
                         @RequestParam String content,
                         @RequestParam(required = false) Integer rating,
                         @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        Course course = courseRepo.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User user = userRepo.findByEmail(principal.getUsername())
                .orElseGet(() -> userRepo.findByUsername(principal.getUsername())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)));

        boolean owns = ownershipRepo.existsByUserIdAndCourseIdAndStatus(
                user.getId(), course.getId(), CourseOwnership.Status.PAID);

        if (!owns)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Trebuie să cumperi cursul pentru a comenta.");

        var existingOpt = commentRepo.findFirstByCourseIdAndUserIdOrderByCreatedAtDesc(course.getId(), user.getId());

        if (existingOpt.isPresent()) {
            CourseComment existing = existingOpt.get();
            existing.setContent(content.trim());
            existing.setRating(rating);
            commentRepo.save(existing);
        } else {
            CourseComment cm = new CourseComment();
            cm.setCourse(course);
            cm.setUser(user);
            cm.setContent(content.trim());
            cm.setRating(rating);
            commentRepo.save(cm);
        }

        return "redirect:/cursuri/" + slug + "#comments";
    }
}
