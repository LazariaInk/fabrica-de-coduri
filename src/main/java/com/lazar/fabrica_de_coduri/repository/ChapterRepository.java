package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByTopicId(Long topicId);
}

