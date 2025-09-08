package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findBySlug(String slug);
}