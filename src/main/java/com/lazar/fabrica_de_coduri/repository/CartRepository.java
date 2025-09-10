package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);
    Optional<Cart> findBySessionKeyAndStatus(String sessionKey, CartStatus status);
}
