package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String role;

    private boolean enabled = false;

    @ManyToMany
    @JoinTable(
            name = "user_premium_courses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "premium_course_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "premium_course_id"})
    )
    private Set<PremiumCourse> purchasedCourses = new HashSet<>();

    public User() {
    }

    public User(String username, String password, String role, boolean enabled) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<PremiumCourse> getPurchasedCourses() {
        return purchasedCourses;
    }

    public void setPurchasedCourses(Set<PremiumCourse> purchasedCourses) {
        this.purchasedCourses = purchasedCourses;
    }
}

