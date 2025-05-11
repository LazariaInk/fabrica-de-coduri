package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int orderNumber;

    @ManyToOne
    private Topic topic;

    @OneToMany(mappedBy = "chapter")
    private List<Lesson> lessons;

    public Chapter() {
    }

    public Chapter(Long id, String title, int orderNumber, Topic topic, List<Lesson> lessons) {
        this.id = id;
        this.title = title;
        this.topic = topic;
        this.lessons = lessons;
        this.orderNumber = orderNumber;
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

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
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


