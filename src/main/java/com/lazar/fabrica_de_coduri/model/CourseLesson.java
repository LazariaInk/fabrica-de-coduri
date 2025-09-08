package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;
@Entity
@Table
public class CourseLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, length = 180)
    private String slug;
    @Column(nullable = false)
    private int position;
    @Column(nullable = false, length = 500)
    private String videoPath;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private CourseChapter chapter;
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public String getVideoPath() { return videoPath; }
    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }
    public CourseChapter getChapter() { return chapter; }
    public void setChapter(CourseChapter chapter) { this.chapter = chapter; }
}