package com.lazar.fabrica_de_coduri.dto;

import com.lazar.fabrica_de_coduri.model.ProgrammingLanguage;
import com.lazar.fabrica_de_coduri.model.StackType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.*;

public class CourseFormDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private ProgrammingLanguage programmingLanguage;
    @NotNull
    private StackType stackType;
    private String tagsCsv;
    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @Size(min = 1)
    private List<CourseChapterFormDTO> chapters = new ArrayList<>();

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProgrammingLanguage getProgrammingLanguage() { return programmingLanguage; }
    public void setProgrammingLanguage(ProgrammingLanguage programmingLanguage) { this.programmingLanguage = programmingLanguage; }
    public StackType getStackType() { return stackType; }
    public void setStackType(StackType stackType) { this.stackType = stackType; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getTagsCsv() { return tagsCsv; }
    public void setTagsCsv(String tagsCsv) { this.tagsCsv = tagsCsv; }
    public List<CourseChapterFormDTO> getChapters() { return chapters; }
    public void setChapters(List<CourseChapterFormDTO> chapters) { this.chapters = chapters; }
}