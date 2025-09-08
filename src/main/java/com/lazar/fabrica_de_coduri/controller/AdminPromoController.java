package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.PromoCode;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;

import com.lazar.fabrica_de_coduri.repository.PromoCodeRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/admin/promo-codes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPromoController {

    private final PromoCodeRepository promoRepo;
    private final CourseRepository courseRepo;

    public AdminPromoController(PromoCodeRepository promoRepo, CourseRepository courseRepo) {
        this.promoRepo = promoRepo;
        this.courseRepo = courseRepo;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("codes", promoRepo.findAll());
        model.addAttribute("newCode", new PromoCode());
        model.addAttribute("courses", courseRepo.findAll());
        return "admin-promo-codes";
    }

    @PostMapping
    public String create(@ModelAttribute("newCode") PromoCode code,
                         @RequestParam(value="courseIds", required=false) List<Long> courseIds) {

        code.setCourses(new HashSet<>());
        if (courseIds != null && !courseIds.isEmpty()) {
            List<Course> list = courseRepo.findAllById(courseIds);
            code.getCourses().addAll(list);
        }
        promoRepo.save(code);
        return "redirect:/admin/promo-codes?success";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id) {
        PromoCode p = promoRepo.findById(id).orElseThrow();
        p.setActive(!p.isActive());
        promoRepo.save(p);
        return "redirect:/admin/promo-codes";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        promoRepo.deleteById(id);
        return "redirect:/admin/promo-codes?deleted";
    }
}
