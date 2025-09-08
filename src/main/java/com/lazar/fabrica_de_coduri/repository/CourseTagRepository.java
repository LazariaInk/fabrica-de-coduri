package com.lazar.fabrica_de_coduri.repository;


import com.lazar.fabrica_de_coduri.model.CourseTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseTagRepository extends JpaRepository<CourseTag, Long> {
    Optional<CourseTag> findBySlug(String slug);
}