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
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "course_authors")
public class CourseAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String slug;

    private String name;

    @Column(length = 300)
    private String headline;

    @Column(length = 1400)
    private String bio;

    private String avatarUrl;

    private String location;

    private String experience;

    private String rating;

    private String students;

    private String linkedinUrl;

    private String githubUrl;

    private String websiteUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_author_specialties", joinColumns = @JoinColumn(name = "author_id"))
    @OrderColumn(name = "position")
    @Column(name = "specialty", length = 180)
    private List<String> specialties;

    public CourseAuthor() {
    }

    public CourseAuthor(String slug, String name, String headline, String bio, String avatarUrl, String location,
                        String experience, String rating, String students, List<String> specialties,
                        String linkedinUrl, String githubUrl, String websiteUrl) {
        this.slug = slug;
        this.name = name;
        this.headline = headline;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.location = location;
        this.experience = experience;
        this.rating = rating;
        this.students = students;
        this.specialties = specialties;
        this.linkedinUrl = linkedinUrl;
        this.githubUrl = githubUrl;
        this.websiteUrl = websiteUrl;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public String getHeadline() {
        return headline;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getLocation() {
        return location;
    }

    public String getExperience() {
        return experience;
    }

    public String getRating() {
        return rating;
    }

    public String getStudents() {
        return students;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public String getInitials() {
        if (name == null || name.isBlank()) {
            return "FC";
        }

        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setStudents(String students) {
        this.students = students;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseAuthor that)) {
            return false;
        }
        return Objects.equals(slug, that.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug);
    }
}
