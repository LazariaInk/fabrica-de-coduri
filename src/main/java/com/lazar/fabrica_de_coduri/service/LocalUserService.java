package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocalUserService {

    @Autowired
    private UserRepository userRepository;  // your JPA repo for User entity

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
