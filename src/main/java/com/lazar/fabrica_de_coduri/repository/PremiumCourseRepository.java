package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.CourseAuthor;
import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PremiumCourseRepository extends JpaRepository<PremiumCourse, Long> {
    Optional<PremiumCourse> findBySlug(String slug);

    List<PremiumCourse> findByAuthorOrderByTitleAsc(CourseAuthor author);
}
