package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findTop8ByIsFeaturedTrueOrderByIdDesc();

    List<Course> findAllByOrderByIdDesc();
    Optional<Course> findBySlug(String slug);
}
