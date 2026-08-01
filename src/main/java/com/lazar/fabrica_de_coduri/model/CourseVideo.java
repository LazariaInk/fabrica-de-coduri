package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.Column;
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
        name = "course_videos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"premium_course_id", "position"})
)
public class CourseVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "premium_course_id", nullable = false)
    private PremiumCourse course;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private int durationSeconds;

    @Column(nullable = false)
    private String storageKey;

    public Long getId() {
        return id;
    }

    public PremiumCourse getCourse() {
        return course;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCourse(PremiumCourse course) {
        this.course = course;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }
}
