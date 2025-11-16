package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.CourseOwnership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseOwnershipRepository extends JpaRepository<CourseOwnership, Long> {
    boolean existsByUserIdAndCourseIdAndStatus(Long userId, Long courseId, CourseOwnership.Status status);
    Optional<CourseOwnership> findByUserIdAndCourseId(Long userId, Long courseId);
    List<CourseOwnership> findByUserIdAndStatus(Long userId, CourseOwnership.Status status);

    List<CourseOwnership> findByUserIdAndStatusOrderByPurchasedAtDesc(
            Long userId,
            CourseOwnership.Status status
    );

    List<CourseOwnership> findByStatus(CourseOwnership.Status status);

}
