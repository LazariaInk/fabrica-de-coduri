package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "premium_courses")
public class PremiumCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String slug;

    private String title;

    @Column(length = 600)
    private String subtitle;

    private String level;

    private String language;

    private String duration;

    private String instructor;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private CourseAuthor author;

    private int lessons;

    private int price;

    private String accentColor;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "premium_course_outcomes", joinColumns = @JoinColumn(name = "course_id"))
    @OrderColumn(name = "position")
    @Column(name = "outcome", length = 500)
    private List<String> outcomes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "premium_course_modules", joinColumns = @JoinColumn(name = "course_id"))
    @OrderColumn(name = "position")
    @Column(name = "module", length = 500)
    private List<String> modules;

    public PremiumCourse() {
    }

    public PremiumCourse(String slug, String title, String subtitle, String level, String duration, String instructor,
                         int lessons, int price, String accentColor, List<String> outcomes, List<String> modules) {
        this(slug, title, subtitle, level, null, duration, instructor, lessons, price, accentColor, outcomes, modules);
    }

    public PremiumCourse(String slug, String title, String subtitle, String level, String language, String duration,
                         String instructor, int lessons, int price, String accentColor, List<String> outcomes,
                         List<String> modules) {
        this.slug = slug;
        this.title = title;
        this.subtitle = subtitle;
        this.level = level;
        this.language = language;
        this.duration = duration;
        this.instructor = instructor;
        this.lessons = lessons;
        this.price = price;
        this.accentColor = accentColor;
        this.outcomes = outcomes;
        this.modules = modules;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getLevel() {
        return level;
    }

    public String getLanguage() {
        return language;
    }

    public String getDuration() {
        return duration;
    }

    public String getInstructor() {
        return instructor;
    }

    public CourseAuthor getAuthor() {
        return author;
    }

    public String getInstructorName() {
        if (author != null && author.getName() != null && !author.getName().isBlank()) {
            return author.getName();
        }

        return instructor;
    }

    public int getLessons() {
        return lessons;
    }

    public int getPrice() {
        return price;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public List<String> getOutcomes() {
        return outcomes;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setAuthor(CourseAuthor author) {
        this.author = author;
    }

    public void setLessons(int lessons) {
        this.lessons = lessons;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public void setOutcomes(List<String> outcomes) {
        this.outcomes = outcomes;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PremiumCourse that)) {
            return false;
        }
        return Objects.equals(slug, that.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug);
    }
}
