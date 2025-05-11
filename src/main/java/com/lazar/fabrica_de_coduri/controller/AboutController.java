package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {
    private final TopicRepository topicRepo;
    private final PlatformInfoRepository platformInfoRepository;

    public AboutController(TopicRepository topicRepo, PlatformInfoRepository platformInfoRepository) {
        this.topicRepo = topicRepo;
        this.platformInfoRepository = platformInfoRepository;
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("topics", topicRepo.findAll());
        model.addAttribute("platformInfo", platformInfoRepository.findById(1L).orElse(null));
        model.addAttribute("message", "Acces interzis. Doar administratorul are permisiune aici.");
        model.addAttribute("content", "error-content");
        return "about";
    }
}
