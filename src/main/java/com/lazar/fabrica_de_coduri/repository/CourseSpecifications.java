package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.Course.Level;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class CourseSpecifications {

    private CourseSpecifications() {}

    public static Specification<Course> build(
            String q,
            String difficulty,
            BigDecimal priceMin,
            BigDecimal priceMax
    ) {
        Specification<Course> spec = Specification.where(null);

        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase().trim() + "%";
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("shortDescription")), like),
                    cb.like(cb.lower(root.get("author")), like),
                    cb.like(cb.lower(root.get("hashtags")), like)
            ));
        }

        if (difficulty != null && !difficulty.isBlank()) {
            try {
                Level level = Level.valueOf(difficulty.toUpperCase());
                spec = spec.and((root, cq, cb) -> cb.equal(root.get("level"), level));
            } catch (IllegalArgumentException ignored) {}
        }

        if (priceMin != null) {
            spec = spec.and((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("price"), priceMin));
        }
        if (priceMax != null) {
            spec = spec.and((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("price"), priceMax));
        }

        return spec;
    }
}
