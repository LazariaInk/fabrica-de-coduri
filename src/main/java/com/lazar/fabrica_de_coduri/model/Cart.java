package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "carts",
        indexes = {
                @Index(name="idx_cart_user_status", columnList = "user_id,status"),
                @Index(name="idx_cart_session_status", columnList = "sessionKey,status")
        })
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 80)
    private String sessionKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CartStatus status = CartStatus.ACTIVE;

    @Column(nullable = false, length = 3)
    private String currency = "RON";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @PreUpdate public void touch() { this.updatedAt = LocalDateTime.now(); }

    public void addItem(CartItem ci) { ci.setCart(this); items.add(ci); }
    public Optional<CartItem> findItemByCourseId(Long courseId) {
        return items.stream().filter(i -> i.getCourse().getId().equals(courseId)).findFirst();
    }

    // getters/setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getSessionKey() { return sessionKey; }
    public void setSessionKey(String sessionKey) { this.sessionKey = sessionKey; }
    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<CartItem> getItems() { return items; }

    public BigDecimal total() {
        return items.stream()
                .map(CartItem::getFinalPrice)    // acum finalPrice == unitPrice; reducerile le adăugăm mai târziu
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
