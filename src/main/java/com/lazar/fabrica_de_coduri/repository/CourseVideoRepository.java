package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.CourseVideo;
import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseVideoRepository extends JpaRepository<CourseVideo, Long> {
    List<CourseVideo> findByCourseOrderByPositionAsc(PremiumCourse course);

    Optional<CourseVideo> findByCourseAndPosition(PremiumCourse course, int position);

    Optional<CourseVideo> findByIdAndCourse(Long id, PremiumCourse course);

    boolean existsByCourse(PremiumCourse course);
}
