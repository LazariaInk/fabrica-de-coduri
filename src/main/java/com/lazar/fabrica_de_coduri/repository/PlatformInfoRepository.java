package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformInfoRepository extends JpaRepository<PlatformInfo, Long> {
    PlatformInfo findTopByOrderByIdAsc();
}
