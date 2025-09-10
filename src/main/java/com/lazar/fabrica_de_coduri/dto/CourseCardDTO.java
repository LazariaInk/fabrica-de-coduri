package com.lazar.fabrica_de_coduri.dto;

import java.math.BigDecimal;
import java.util.List;

public class CourseCardDTO {
    public Long id;
    public String title;
    public String slug;
    public String stack;
    public String language;
    public String description;
    public String image;
    public List<String> tags;
    public String url;
    public String lastUpdated;

    // ✅ PREȚ în RON (major units)
    public BigDecimal price;         // ex: 129.00
    public String currency = "RON";  // dacă nu folosești multi-currency, rămâne RON

    // Cuprins (dacă îl expui deja)
    public List<ChapterItem> chapters;

    public CourseCardDTO(Long id, String title, String slug, String stack, String language,
                         String description, String image, List<String> tags, String url, String lastUpdated) {
        this.id = id; this.title = title; this.slug = slug; this.stack = stack; this.language = language;
        this.description = description; this.image = image; this.tags = tags; this.url = url; this.lastUpdated = lastUpdated;
    }

    public static class ChapterItem {
        public Integer position;
        public String title;
        public String description;
        public List<LessonItem> lessons;
    }
    public static class LessonItem {
        public Integer position;
        public String title;
    }
}
