package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {

}
