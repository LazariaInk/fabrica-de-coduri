package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private TopicRepository topicRepo;
    @Autowired
    private PlatformInfoRepository platformInfoRepository;


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
