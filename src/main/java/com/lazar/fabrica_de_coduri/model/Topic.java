package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Topic {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    private String codeSnippet;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }
}

