package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConsetamantController {

    private final TopicRepository topicRepo;
    private final PlatformInfoRepository platformInfoRepository;

    public ConsetamantController(TopicRepository topicRepo, PlatformInfoRepository platformInfoRepository) {
        this.topicRepo = topicRepo;
        this.platformInfoRepository = platformInfoRepository;
    }

    @GetMapping("/politica-confidentialitate")
    public String politicaConfidentialitatii(Model model) {
        model.addAttribute("topics", topicRepo.findAll());
        model.addAttribute("platformInfo", platformInfoRepository.findById(1L).orElse(null));
        model.addAttribute("message", "Acces interzis. Doar administratorul are permisiune aici.");
        model.addAttribute("content", "error-content");
        return "politica-confidentialitate";
    }

    @GetMapping("/termeni-conditii")
    public String termeniSiConditii(Model model) {
        model.addAttribute("topics", topicRepo.findAll());
        model.addAttribute("platformInfo", platformInfoRepository.findById(1L).orElse(null));
        model.addAttribute("message", "Acces interzis. Doar administratorul are permisiune aici.");
        model.addAttribute("content", "error-content");
        return "termeni-conditii";
    }
}
