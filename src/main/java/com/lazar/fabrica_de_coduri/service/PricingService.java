package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.PromoCode;
import com.lazar.fabrica_de_coduri.model.PromoRedemption;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;
import com.lazar.fabrica_de_coduri.repository.PromoCodeRepository;
import com.lazar.fabrica_de_coduri.repository.PromoRedemptionRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class PricingService {

    private final PromoCodeRepository promoRepo;
    private final PromoRedemptionRepository redemptionRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;

    public PricingService(PromoCodeRepository promoRepo,
                          PromoRedemptionRepository redemptionRepo,
                          CourseRepository courseRepo,
                          UserRepository userRepo) {
        this.promoRepo = promoRepo;
        this.redemptionRepo = redemptionRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
    }

    public BigDecimal basePrice(Course c) {
        return c.getPrice() == null ? BigDecimal.ZERO : c.getPrice();
    }

    public BigDecimal previewWithPromo(Course c, PromoCode promo) {
        BigDecimal price = basePrice(c);
        BigDecimal pct = promo.getPercent() == null ? BigDecimal.ZERO : promo.getPercent();
        if (pct.compareTo(BigDecimal.ZERO) < 0 || pct.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Procent invalid (0..100).");
        }
        BigDecimal factor = BigDecimal.ONE.subtract(pct.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        BigDecimal discounted = price.multiply(factor);
        if (discounted.compareTo(BigDecimal.ZERO) < 0) discounted = BigDecimal.ZERO;
        return discounted.setScale(2, RoundingMode.HALF_UP);
    }

    private void validate(PromoCode promo, Course course, User user) {
        if (!promo.isActive()) throw new IllegalArgumentException("Codul promo nu este activ.");
        if (!promo.inWindow(LocalDateTime.now())) throw new IllegalArgumentException("Codul promo nu este valabil acum.");
        if (!promo.appliesTo(course)) throw new IllegalArgumentException("Codul promo nu se aplică acestui curs.");

        if (promo.getMaxRedemptions() != null) {
            long used = redemptionRepo.countByPromoCode(promo);
            if (used >= promo.getMaxRedemptions())
                throw new IllegalArgumentException("S-a atins limita globală de utilizări.");
        }
        if (promo.getPerUserLimit() != null) {
            long usedByUser = redemptionRepo.countByPromoCodeAndUserAndCourse(promo, user, course);
            if (usedByUser >= promo.getPerUserLimit())
                throw new IllegalArgumentException("Ai atins limita de utilizări pentru acest cod pe acest curs.");
        }
    }

    @Transactional
    public BigDecimal applyPromo(Long userId, Long courseId, String rawCode) {
        PromoCode promo = promoRepo.findByCodeIgnoreCase(rawCode)
                .orElseThrow(() -> new IllegalArgumentException("Cod promo inexistent."));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curs inexistent."));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizator inexistent."));

        validate(promo, course, user);

        BigDecimal finalPrice = previewWithPromo(course, promo);

        PromoRedemption redemption = redemptionRepo.findByUserAndCourse(user, course)
                .orElseGet(PromoRedemption::new);
        redemption.setUser(user);
        redemption.setCourse(course);
        redemption.setPromoCode(promo);
        redemption.setFinalPrice(finalPrice);
        redemption.setRedeemedAt(LocalDateTime.now());
        redemptionRepo.save(redemption);

        return finalPrice;
    }

    public BigDecimal currentPriceForUserCourse(Long userId, Long courseId) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        User user = userRepo.findById(userId).orElseThrow();
        return redemptionRepo.findByUserAndCourse(user, course)
                .map(PromoRedemption::getFinalPrice)
                .orElseGet(() -> basePrice(course));
    }
}
