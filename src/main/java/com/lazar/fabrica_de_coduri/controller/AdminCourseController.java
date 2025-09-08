package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.dto.CourseChapterFormDTO;
import com.lazar.fabrica_de_coduri.dto.CourseFormDTO;
import com.lazar.fabrica_de_coduri.dto.CourseLessonFormDTO;
import com.lazar.fabrica_de_coduri.model.ProgrammingLanguage;
import com.lazar.fabrica_de_coduri.model.StackType;
import com.lazar.fabrica_de_coduri.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin/courses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {
    private final CourseService courseService;
    public AdminCourseController(CourseService courseService) {
        this.courseService = courseService;
    }


    @GetMapping("/new")
    public String newCourseForm(Model model) {
        CourseFormDTO form = new CourseFormDTO();

        CourseChapterFormDTO ch = new CourseChapterFormDTO();
        CourseLessonFormDTO ls = new CourseLessonFormDTO();
        ch.getLessons().add(ls);
        form.getChapters().add(ch);


        model.addAttribute("form", form);
        model.addAttribute("languages", ProgrammingLanguage.values());
        model.addAttribute("stacks", StackType.values());
        model.addAttribute("canonicalUrl", "/admin/courses/new");
        return "admin-course-form";
    }


    @PostMapping
    public String createCourse(@Valid @ModelAttribute("form") CourseFormDTO form,
                               BindingResult binding,
                               Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("languages", ProgrammingLanguage.values());
            model.addAttribute("stacks", StackType.values());
            return "admin-course-form";
        }
        courseService.createCourseFromForm(form);
        return "redirect:/admin/courses/new?success";
    }
}