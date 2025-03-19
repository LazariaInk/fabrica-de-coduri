package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.SEOHashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SEOHashTagRepository extends JpaRepository<SEOHashTag, Long> {
    List<SEOHashTag> findByPlatformInfoId(Long platformInfoId);
}