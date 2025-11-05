
package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;
import java.util.*;

@Entity @Table(name = "course_chapters")
public class CourseChapter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 600)
    private String description;

    @Column(nullable = false)
    private Integer position = 0; // ordinea capitolului în curs

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<CourseLesson> lessons = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<CourseLesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<CourseLesson> lessons) {
        this.lessons = lessons;
    }
}
