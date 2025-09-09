package com.lazar.fabrica_de_coduri.model;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;


@Entity
@Table(indexes = {@Index(name = "idx_course_slug", columnList = "slug", unique = true)})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, length = 180)
    private String slug;
    @Column(nullable = false, length = 2000)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ProgrammingLanguage programmingLanguage;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StackType stackType;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "course_tags",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<CourseTag> tags = new LinkedHashSet<>();
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<CourseChapter> chapters = new ArrayList<>();
    @Column(length = 500)
    private String coverImagePath;
    @Column(length = 150)
    private String coverImageAlt;
    @Column(nullable=false, precision=19, scale=2)
    private BigDecimal price = BigDecimal.ZERO;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private boolean published = false;

    public void addChapter(CourseChapter c) {
        c.setCourse(this);
        this.chapters.add(c);
    }

    public void removeChapter(CourseChapter c) {
        c.setCourse(null);
        this.chapters.remove(c);
    }
    @PreUpdate
    public void onUpdate() { this.updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCoverImagePath() { return coverImagePath; }
    public void setCoverImagePath(String coverImagePath) { this.coverImagePath = coverImagePath; }
    public String getCoverImageAlt() { return coverImageAlt; }
    public void setCoverImageAlt(String coverImageAlt) { this.coverImageAlt = coverImageAlt; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProgrammingLanguage getProgrammingLanguage() { return programmingLanguage; }
    public void setProgrammingLanguage(ProgrammingLanguage programmingLanguage) { this.programmingLanguage = programmingLanguage; }
    public StackType getStackType() { return stackType; }
    public void setStackType(StackType stackType) { this.stackType = stackType; }
    public Set<CourseTag> getTags() { return tags; }
    public void setTags(Set<CourseTag> tags) { this.tags = tags; }
    public List<CourseChapter> getChapters() { return chapters; }
    public void setChapters(List<CourseChapter> chapters) { this.chapters = chapters; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public BigDecimal getPrice(){ return price; }
    public void setPrice(BigDecimal price){ this.price = price; }
}