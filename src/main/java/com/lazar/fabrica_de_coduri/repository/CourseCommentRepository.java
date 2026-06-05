package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.CourseComment;
import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import com.lazar.fabrica_de_coduri.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseCommentRepository extends JpaRepository<CourseComment, Long> {
    List<CourseComment> findByCourseOrderByUpdatedAtDesc(PremiumCourse course);

    Optional<CourseComment> findByUserAndCourse(User user, PremiumCourse course);
}
