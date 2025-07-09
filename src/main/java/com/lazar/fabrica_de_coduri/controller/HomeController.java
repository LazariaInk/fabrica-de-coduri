package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private TopicRepository topicRepo;
    @Autowired
    private PlatformInfoRepository platformInfoRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("topics", topicRepo.findAll());

        PlatformInfo platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);

        return "home";
    }
}