package com.lazar.fabrica_de_coduri.repository;


import com.lazar.fabrica_de_coduri.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Optional<Lesson> findBySlugAndChapter_SlugAndChapter_Topic_Slug(
            String lessonSlug,
            String chapterSlug,
            String topicSlug
    );
}

