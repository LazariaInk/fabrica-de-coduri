package com.lazar.fabrica_de_coduri.model;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name="uk_redemption_user_course", columnNames={"user_id","course_id"})})
public class PromoRedemption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="promo_id")
    private PromoCode promoCode;

    @ManyToOne(optional=false) @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(optional=false) @JoinColumn(name="course_id")
    private Course course;

    @Column(nullable=false, precision=19, scale=2)
    private BigDecimal finalPrice;
    @Column(nullable=false)
    private LocalDateTime redeemedAt = LocalDateTime.now();
    public Long getId(){ return id; }
    public PromoCode getPromoCode(){ return promoCode; }
    public void setPromoCode(PromoCode promoCode){ this.promoCode = promoCode; }
    public User getUser(){ return user; }
    public void setUser(User user){ this.user = user; }
    public Course getCourse(){ return course; }
    public void setCourse(Course course){ this.course = course; }
    public BigDecimal getFinalPrice(){ return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice){ this.finalPrice = finalPrice; }
    public LocalDateTime getRedeemedAt(){ return redeemedAt; }
    public void setRedeemedAt(LocalDateTime redeemedAt){ this.redeemedAt = redeemedAt; }
}
