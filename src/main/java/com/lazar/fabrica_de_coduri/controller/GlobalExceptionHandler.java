package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private TopicRepository topicRepo;
    @Autowired
    private PlatformInfoRepository platformInfoRepository;
    @Autowired
    private UserRepository userRepository;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleUploadTooLarge(MaxUploadSizeExceededException ex, Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            userRepository.findByUsername(authentication.getName())
                    .ifPresent(user -> model.addAttribute("user", user));
        }
        model.addAttribute("avatarError", "Poza este prea mare. Alege o imagine de maximum 5MB.");
        return "account-settings";
    }

    @ExceptionHandler(Exception.class)
    public String handleAnyException(Exception ex, Model model) {
        model.addAttribute("topics", topicRepo.findAll());

        PlatformInfo platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("content", "error-content");
        return "error-page";
    }
}
