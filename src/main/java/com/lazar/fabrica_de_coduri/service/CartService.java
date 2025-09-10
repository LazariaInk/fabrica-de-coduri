package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.*;
import com.lazar.fabrica_de_coduri.repository.CartRepository;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartService {
    private final CartRepository cartRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;

    public CartService(CartRepository cartRepo, CourseRepository courseRepo, UserRepository userRepo) {
        this.cartRepo = cartRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public Cart ensureActiveCart(Long userId, String sessionKey) {
        if (userId != null) {
            User u = userRepo.findById(userId).orElseThrow();
            return cartRepo.findByUserAndStatus(u, CartStatus.ACTIVE)
                    .orElseGet(() -> {
                        Cart c = new Cart();
                        c.setUser(u); c.setCurrency("RON");
                        return cartRepo.save(c);
                    });
        }
        // guest
        return cartRepo.findBySessionKeyAndStatus(sessionKey, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setSessionKey(sessionKey); c.setCurrency("RON");
                    return cartRepo.save(c);
                });
    }

    @Transactional
    public Cart addCourse(Long userId, String sessionKey, Long courseId) {
        Cart cart = ensureActiveCart(userId, sessionKey);
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curs inexistent"));

        if (cart.findItemByCourseId(courseId).isPresent()) return cart;

        BigDecimal price = course.getPrice() == null ? BigDecimal.ZERO : course.getPrice();

        CartItem item = new CartItem();
        item.setCourse(course);
        item.setUnitPrice(price);
        item.setFinalPrice(price);
        cart.addItem(item);

        return cartRepo.save(cart);
    }

    @Transactional
    public Cart removeCourse(Long userId, String sessionKey, Long courseId) {
        Cart cart = ensureActiveCart(userId, sessionKey);
        cart.findItemByCourseId(courseId).ifPresent(ci -> cart.getItems().remove(ci));
        return cartRepo.save(cart);
    }
}
