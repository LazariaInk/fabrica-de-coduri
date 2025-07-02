package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
