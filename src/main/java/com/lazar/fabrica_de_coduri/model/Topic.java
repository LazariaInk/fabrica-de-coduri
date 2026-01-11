package com.lazar.fabrica_de_coduri.model;

import com.lazar.fabrica_de_coduri.utils.SlugUtils;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String codeSnippet;
    @Column(nullable = false, unique = true)
    private String slug;

    @OneToMany(mappedBy = "topic")
    private List<Chapter> chapters;

    public Topic() {
    }

    public Topic(Long id, String name, List<Chapter> chapters, String description, String codeSnippet) {
        this.id = id;
        this.name = name;
        this.chapters = chapters;
        this.description = description;
        this.codeSnippet = codeSnippet;
    }

    public Topic(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public String getDescription() {
        return description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }
    @PrePersist
    @PreUpdate
    private void ensureSlug() {
        if (this.name != null) {
            this.slug = SlugUtils.toSlug(this.name);
        }
    }
}

