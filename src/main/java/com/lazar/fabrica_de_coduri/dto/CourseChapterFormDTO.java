package com.lazar.fabrica_de_coduri.dto;

import jakarta.validation.constraints.*;
import java.util.*;


public class CourseChapterFormDTO {
    @NotBlank
    private String title;
    private String description;
    private Integer position;
    @Size(min = 1)
    private List<CourseLessonFormDTO> lessons = new ArrayList<>();
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public List<CourseLessonFormDTO> getLessons() { return lessons; }
    public void setLessons(List<CourseLessonFormDTO> lessons) { this.lessons = lessons; }
}