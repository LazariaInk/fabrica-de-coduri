package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "user_course_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "premium_course_id"})
)
public class UserCourseProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "premium_course_id", nullable = false)
    private PremiumCourse course;

    private int completedLessons;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public PremiumCourse getCourse() {
        return course;
    }

    public int getCompletedLessons() {
        return completedLessons;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCourse(PremiumCourse course) {
        this.course = course;
    }

    public void setCompletedLessons(int completedLessons) {
        this.completedLessons = completedLessons;
    }
}
