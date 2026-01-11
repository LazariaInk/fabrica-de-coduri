package com.lazar.fabrica_de_coduri.model;

import com.lazar.fabrica_de_coduri.utils.SlugUtils;
import jakarta.persistence.*;

@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int orderNumber;
    private String htmlPath;

    @ManyToOne
    private Chapter chapter;

    @Column(nullable = false)
    private String slug;

    public Lesson() {
    }

    public Lesson(Long id, String title, int orderNumber, String htmlPath, Chapter chapter) {
        this.id = id;
        this.title = title;
        this.htmlPath = htmlPath;
        this.chapter = chapter;
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

    public String getHtmlPath() {
        return htmlPath;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }
    @PrePersist
    @PreUpdate
    private void ensureSlug() {
        if (this.title != null) {
            this.slug = SlugUtils.toSlug(this.title);
        }
    }

}
