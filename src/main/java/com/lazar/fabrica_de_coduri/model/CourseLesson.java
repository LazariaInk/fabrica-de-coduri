package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;

@Entity @Table(name = "course_lessons")
public class CourseLesson {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "chapter_id", nullable = false)
    private CourseChapter chapter;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 300)
    private String videoUrl;

    @Column(name="duration_minutes")
    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer position = 0;

    @Column(nullable = false)
    private Boolean freePreview = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseChapter getChapter() {
        return chapter;
    }

    public void setChapter(CourseChapter chapter) {
        this.chapter = chapter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getFreePreview() {
        return freePreview;
    }

    public void setFreePreview(Boolean freePreview) {
        this.freePreview = freePreview;
    }
}
