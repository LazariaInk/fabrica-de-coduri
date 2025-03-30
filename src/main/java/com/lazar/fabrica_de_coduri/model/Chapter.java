package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Chapter {
    @Id
    @GeneratedValue
    private Long id;
    private String title;

    @ManyToOne
    private Topic topic;

    @OneToMany(mappedBy = "chapter")
    private List<Lesson> lessons;

    public Chapter() {
    }

    public Chapter(Long id, String title, Topic topic, List<Lesson> lessons) {
        this.id = id;
        this.title = title;
        this.topic = topic;
        this.lessons = lessons;
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

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}


