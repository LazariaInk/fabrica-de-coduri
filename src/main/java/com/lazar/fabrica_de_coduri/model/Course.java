package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;


@Entity
@Table(name = "courses")
public class Course {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=200)
    private String title;

    @Column(nullable=false, length=200, unique = true)
    private String slug;

    @Column(name="cover_path", nullable=false, length=300)
    private String coverPath;

    @Column(nullable=false, length=120)
    private String author;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name="duration_minutes", nullable=false)
    private Integer durationMinutes;

    @Column(nullable=false, length=80)
    private String technology;

    @Column(length=400)
    private String hashtags;

    @Enumerated(EnumType.STRING)
    private Level level = Level.BEGINNER;

    @Column(name="short_description", length=600)
    private String shortDescription;

    @Column(name="is_featured")
    private Boolean isFeatured = false;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<CourseChapter> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<CourseComment> comments = new ArrayList<>();

    @Transient
    public String getDurationLabel() {
        if (durationMinutes == null) return null;
        int h = durationMinutes / 60, m = durationMinutes % 60;
        return (h > 0 ? h + "h " : "") + (m > 0 ? m + "m" : (h == 0 ? "0m" : ""));
    }

    public Course() {
    }

    public Course(Long id, String title, String slug, String coverPath
            , String author, BigDecimal price, Integer durationMinutes
            , String technology, String hashtags, Level level
            , String shortDescription, Boolean isFeatured, List<CourseChapter> chapters, List<CourseComment> comments) {
        this.id = id;
        this.chapters = chapters;
        this.comments = comments;
        this.title = title;
        this.slug = slug;
        this.coverPath = coverPath;
        this.author = author;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.technology = technology;
        this.hashtags = hashtags;
        this.level = level;
        this.shortDescription = shortDescription;
        this.isFeatured = isFeatured;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public List<CourseChapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<CourseChapter> chapters) {
        this.chapters = chapters;
    }

    public List<CourseComment> getComments() {
        return comments;
    }

    public void setComments(List<CourseComment> comments) {
        this.comments = comments;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Boolean getFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        isFeatured = featured;
    }

    public enum Level { BEGINNER, INTERMEDIATE, ADVANCED }
}
