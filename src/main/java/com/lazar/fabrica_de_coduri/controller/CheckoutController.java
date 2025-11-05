package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.CourseOwnership;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.paypal.PayPalService;
import com.lazar.fabrica_de_coduri.repository.CourseOwnershipRepository;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class CheckoutController {

    private final CourseRepository courseRepo;
    private final PayPalService payPalService;
    private final UserRepository userRepository;
    private final CourseOwnershipRepository courseOwnershipRepository;

    @Value("${paypal.client-id}") String paypalClientId;
    @Value("${paypal.currency}") String paypalCurrency;

    public CheckoutController(CourseRepository courseRepo, PayPalService payPalService, UserRepository userRepository
    ,CourseOwnershipRepository courseOwnershipRepository) {
        this.courseRepo = courseRepo;
        this.payPalService = payPalService;
        this.userRepository = userRepository;
        this.courseOwnershipRepository = courseOwnershipRepository;
    }

    @GetMapping("/checkout/{slug}")
    @PreAuthorize("isAuthenticated()")
    public String checkout(@PathVariable String slug,
                           Model model,
                           @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        Course c = courseRepo.findBySlug(slug)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND));

        var user = userRepository.findByEmail(principal.getUsername())
                .orElseGet(() -> userRepository.findByUsername(principal.getUsername())
                        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.UNAUTHORIZED)));

        boolean alreadyHas = courseOwnershipRepository.existsByUserIdAndCourseIdAndStatus(
                user.getId(), c.getId(), CourseOwnership.Status.PAID);

        if (alreadyHas) {
            return "redirect:/cursuri/" + slug;
        }

        model.addAttribute("course", c);
        model.addAttribute("paypalClientId", paypalClientId);
        model.addAttribute("paypalCurrency", paypalCurrency);
        return "checkout";
    }

    @PostMapping("/api/paypal/orders/{slug}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> createOrder(@PathVariable String slug,
                                         @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        Map<String, Object> resp = payPalService.createOrder(slug, principal.getUsername());
        return ResponseEntity.ok(resp);
    }

    // API: capture order
    @PostMapping("/api/paypal/capture/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> capture(@PathVariable String orderId,
                                     @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        Map<String, Object> resp = payPalService.captureOrder(orderId, principal.getUsername());
        return ResponseEntity.ok(resp);
    }
}
