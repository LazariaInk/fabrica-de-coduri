package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name="uk_promocode_code", columnNames="code")})
public class PromoCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length=64, nullable=false)
    private String code;
    private String description;
    @Column(nullable=false, precision=5, scale=2)
    private BigDecimal percent;
    private boolean active = true;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private Integer maxRedemptions;
    private Integer perUserLimit = 1;
    @ManyToMany
    @JoinTable(
            name="promo_code_courses",
            joinColumns=@JoinColumn(name="promo_id"),
            inverseJoinColumns=@JoinColumn(name="course_id")
    )
    private Set<Course> courses = new HashSet<>();

    @PrePersist @PreUpdate
    public void normalize() {
        if (code != null) code = code.trim().toUpperCase();
    }
    public Long getId(){ return id; }
    public String getCode(){ return code; }
    public void setCode(String code){ this.code = code; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    public BigDecimal getPercent(){ return percent; }
    public void setPercent(BigDecimal percent){ this.percent = percent; }
    public boolean isActive(){ return active; }
    public void setActive(boolean active){ this.active = active; }
    public LocalDateTime getStartsAt(){ return startsAt; }
    public void setStartsAt(LocalDateTime startsAt){ this.startsAt = startsAt; }
    public LocalDateTime getEndsAt(){ return endsAt; }
    public void setEndsAt(LocalDateTime endsAt){ this.endsAt = endsAt; }
    public Integer getMaxRedemptions(){ return maxRedemptions; }
    public void setMaxRedemptions(Integer maxRedemptions){ this.maxRedemptions = maxRedemptions; }
    public Integer getPerUserLimit(){ return perUserLimit; }
    public void setPerUserLimit(Integer perUserLimit){ this.perUserLimit = perUserLimit; }
    public Set<Course> getCourses(){ return courses; }
    public void setCourses(Set<Course> courses){ this.courses = courses; }

    public boolean inWindow(LocalDateTime now){
        if (startsAt != null && now.isBefore(startsAt)) return false;
        if (endsAt != null && now.isAfter(endsAt)) return false;
        return true;
    }
    public boolean appliesTo(Course c){
        return courses.isEmpty() || courses.contains(c);
    }
}
