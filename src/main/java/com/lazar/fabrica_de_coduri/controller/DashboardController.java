package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private TopicRepository topicRepo;
    @Autowired
    private PlatformInfoRepository platformInfoRepository;

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("topics", topicRepo.findAll());
        model.addAttribute("platformInfo", platformInfoRepository.findById(1L).orElse(null));
        model.addAttribute("canonicalUrl", "https://fabricadecoduri.ro/");
        return "courses";
    }

    @GetMapping("/edit-profile")
    public String editProfile() {
        return "edit-profile";
    }
}