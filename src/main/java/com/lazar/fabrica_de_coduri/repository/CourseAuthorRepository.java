package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.CourseAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseAuthorRepository extends JpaRepository<CourseAuthor, Long> {
    Optional<CourseAuthor> findBySlug(String slug);
}
