package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.CourseComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    List<Course> findTop8ByIsFeaturedTrueOrderByIdDesc();

    List<Course> findAllByOrderByIdDesc();
    Optional<Course> findBySlug(String slug);
}
