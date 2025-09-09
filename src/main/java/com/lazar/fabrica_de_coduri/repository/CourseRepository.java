// src/main/java/com/lazar/fabrica_de_coduri/repository/CourseRepository.java
package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.Course;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    Optional<Course> findBySlug(String slug);

}
