package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.model.VerificationToken;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import com.lazar.fabrica_de_coduri.repository.VerificationTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

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

//    @Controller
//    public static class ConfirmationController {
//
//        private final VerificationTokenRepository tokenRepository;
//        private final UserRepository userRepository;
//
//        public ConfirmationController(VerificationTokenRepository tokenRepository,
//                                      UserRepository userRepository) {
//            this.tokenRepository = tokenRepository;
//            this.userRepository = userRepository;
//        }
//
//        @GetMapping("/confirm")
//        public String confirmEmail(@RequestParam("token") String token, Model model) {
//            Optional<VerificationToken> optional = tokenRepository.findByToken(token);
//
//            if (optional.isEmpty()) {
//                model.addAttribute("message", "Invalid token!");
//                return "confirm";
//            }
//
//            VerificationToken verificationToken = optional.get();
//
//            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
//                model.addAttribute("message", "Token expired!");
//                return "confirm";
//            }
//
//            User user = verificationToken.getUser();
//            user.setEnabled(true);
//            userRepository.save(user);
//            tokenRepository.delete(verificationToken);
//
//            model.addAttribute("message", "Account confirmed. You can now log in.");
//            return "confirm";
//        }
//    }
}