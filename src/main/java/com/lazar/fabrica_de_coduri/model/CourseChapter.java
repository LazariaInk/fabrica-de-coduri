package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table
public class CourseChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, length = 180)
    private String slug;
    @Column(length = 1000)
    private String description;
    @Column(nullable = false)
    private int position;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<CourseLesson> lessons = new ArrayList<>();


    public void addLesson(CourseLesson l) {
        l.setChapter(this);
        this.lessons.add(l);
    }
    public void removeLesson(CourseLesson l) {
        l.setChapter(null);
        this.lessons.remove(l);
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public List<CourseLesson> getLessons() { return lessons; }
    public void setLessons(List<CourseLesson> lessons) { this.lessons = lessons; }
}