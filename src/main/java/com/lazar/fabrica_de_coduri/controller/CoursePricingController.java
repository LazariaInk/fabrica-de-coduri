package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.repository.UserRepository;
import com.lazar.fabrica_de_coduri.service.PricingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/courses")
public class CoursePricingController {

    private final PricingService pricing;
    private final UserRepository userRepo;

    public CoursePricingController(PricingService pricing, UserRepository userRepo) {
        this.pricing = pricing;
        this.userRepo = userRepo;
    }

    @PostMapping("/{courseId}/promo/apply")
    public String applyPromo(@PathVariable Long courseId,
                             @RequestParam String code,
                             @AuthenticationPrincipal UserDetails principal) {
        try {
            Long userId = userRepo.findByEmail(principal.getUsername()).orElseThrow().getId();
            pricing.applyPromo(userId, courseId, code);
            return "redirect:/courses/" + courseId + "?promo_applied";
        } catch (Exception ex) {
            return "redirect:/courses/" + courseId + "?promo_error=" + ex.getMessage();
        }
    }
}
