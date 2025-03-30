package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TopicRepository extends JpaRepository<Topic, Long> {
}
