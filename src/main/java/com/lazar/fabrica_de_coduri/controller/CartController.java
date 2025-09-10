package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.dto.*;
import com.lazar.fabrica_de_coduri.model.Cart;
import com.lazar.fabrica_de_coduri.service.CartService;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepo;

    public CartController(CartService cartService, UserRepository userRepo) {
        this.cartService = cartService;
        this.userRepo = userRepo;
    }

    private Long currentUserId(Principal principal) {
        if (principal == null) return null;
        // ajustează după cum ții identitatea: username vs email
        return userRepo.findByUsername(principal.getName())
                .or(() -> userRepo.findByEmail(principal.getName()))
                .map(u -> u.getId()).orElse(null);
    }

    private CartSummaryDTO toDto(Cart cart) {
        var items = cart.getItems().stream()
                .map(ci -> new CartItemDTO(ci.getCourse().getId(), ci.getCourse().getTitle(), ci.getFinalPrice()))
                .collect(Collectors.toList());
        return new CartSummaryDTO(items.size(), cart.total(), items);
    }

    @PostMapping("/items")
    public CartSummaryDTO addItem(@RequestBody AddToCartRequest req, HttpSession session, Principal principal) {
        Long uid = currentUserId(principal);
        Cart cart = cartService.addCourse(uid, session.getId(), req.courseId());
        return toDto(cart);
    }

    @DeleteMapping("/items/{courseId}")
    public CartSummaryDTO removeItem(@PathVariable Long courseId, HttpSession session, Principal principal) {
        Long uid = currentUserId(principal);
        Cart cart = cartService.removeCourse(uid, session.getId(), courseId);
        return toDto(cart);
    }

    @GetMapping
    public CartSummaryDTO getCart(HttpSession session, Principal principal) {
        Long uid = currentUserId(principal);
        Cart cart = cartService.ensureActiveCart(uid, session.getId());
        return toDto(cart);
    }
}
