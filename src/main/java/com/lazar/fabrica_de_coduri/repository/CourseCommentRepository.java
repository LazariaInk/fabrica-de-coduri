package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.CourseComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseCommentRepository extends JpaRepository<CourseComment, Long> {
    @Query("select c from CourseComment c where c.course.id=:courseId " +
            "order by coalesce(c.updatedAt, c.createdAt) desc")
    List<CourseComment> findAllForCourseOrdered(@Param("courseId") Long courseId);

    Optional<CourseComment> findFirstByCourseIdAndUserIdOrderByCreatedAtDesc(Long courseId, Long userId);
}
