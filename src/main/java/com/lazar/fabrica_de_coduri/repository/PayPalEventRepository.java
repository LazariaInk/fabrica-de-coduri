package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.PayPalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayPalEventRepository extends JpaRepository<PayPalEvent, Long> {
    boolean existsByEventId(String eventId);
    boolean existsByCaptureId(String captureId);
    Optional<PayPalEvent> findByCaptureId(String captureId);
}
