package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.PromoCode;
import com.lazar.fabrica_de_coduri.model.PromoRedemption;
import com.lazar.fabrica_de_coduri.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromoRedemptionRepository extends JpaRepository<PromoRedemption, Long> {
    long countByPromoCode(PromoCode promo);
    long countByPromoCodeAndUserAndCourse(PromoCode promo, User user, Course course);
    Optional<PromoRedemption> findByUserAndCourse(User user, Course course);
}
