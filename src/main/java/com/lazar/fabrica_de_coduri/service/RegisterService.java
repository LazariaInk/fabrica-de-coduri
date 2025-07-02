package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.RegisterDTO;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.model.VerificationToken;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import com.lazar.fabrica_de_coduri.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegisterService {

        private final UserRepository userRepository;
        private final VerificationTokenRepository tokenRepository;
        private final EmailService emailService;
        private final PasswordEncoder encoder;

    public RegisterService(UserRepository userRepository, VerificationTokenRepository tokenRepository, EmailService emailService, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.encoder = encoder;
    }

    public String registerUser(RegisterDTO dto, String appUrl) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return "User already exists";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnabled(false);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        String confirmationUrl = appUrl + "/confirm?token=" + token;

        emailService.sendEmail(user.getUsername(), "Confirm your registration",
                "Click the link to confirm your account: " + confirmationUrl);

        return null;
    }

    public String confirmUser(String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return "Invalid token";
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token expired";
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return null;
    }
    }

