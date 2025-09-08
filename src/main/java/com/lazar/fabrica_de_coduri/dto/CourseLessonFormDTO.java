package com.lazar.fabrica_de_coduri.dto;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class CourseLessonFormDTO {
    @NotBlank
    private String title;
    private Integer position;
    @NotNull
    private MultipartFile videoFile;
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public MultipartFile getVideoFile() { return videoFile; }
    public void setVideoFile(MultipartFile videoFile) { this.videoFile = videoFile; }
}