package com.lazar.fabrica_de_coduri.model;

import java.util.List;

public class PremiumCourse {
    private final String slug;
    private final String title;
    private final String subtitle;
    private final String level;
    private final String duration;
    private final String instructor;
    private final int lessons;
    private final int price;
    private final String accentColor;
    private final List<String> outcomes;
    private final List<String> modules;

    public PremiumCourse(String slug, String title, String subtitle, String level, String duration, String instructor,
                         int lessons, int price, String accentColor, List<String> outcomes, List<String> modules) {
        this.slug = slug;
        this.title = title;
        this.subtitle = subtitle;
        this.level = level;
        this.duration = duration;
        this.instructor = instructor;
        this.lessons = lessons;
        this.price = price;
        this.accentColor = accentColor;
        this.outcomes = outcomes;
        this.modules = modules;
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

    public String getDuration() {
        return duration;
    }

    public String getInstructor() {
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
}
