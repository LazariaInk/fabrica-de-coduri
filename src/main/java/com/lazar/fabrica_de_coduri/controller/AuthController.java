package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.PasswordResetToken;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import com.lazar.fabrica_de_coduri.service.EmailService;
import com.lazar.fabrica_de_coduri.service.LocalUserService;
import com.lazar.fabrica_de_coduri.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalUserService localUserService;

    @Autowired
    private PasswordResetTokenService tokenService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage() {
        return "reset-password-request";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOpt = localUserService.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            PasswordResetToken token = tokenService.createTokenForUser(user);
            String resetLink = "http://localhost:8080/change-password?token=" + token.getToken();

            emailService.sendResetPasswordEmail(user.getEmail(), resetLink);
        }

        model.addAttribute("message", "Daca asa email exista, vei primi un email cu intructiuni.");
        return "reset-password-request";
    }

    @GetMapping("/change-password")
    public String showChangePasswordPage(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> resetTokenOpt = tokenService.getToken(token);

        if (resetTokenOpt.isEmpty() || resetTokenOpt.get().isExpired()) {
            model.addAttribute("message", "Invalid or expired token.");
            return "login"; // or error page
        }

        model.addAttribute("token", token);
        return "change-password-form";
    }

    @PostMapping("/change-password")
    public String processChangePassword(@RequestParam("token") String token,
                                        @RequestParam("password") String password,
                                        Model model) {
        Optional<PasswordResetToken> resetTokenOpt = tokenService.getToken(token);

        if (resetTokenOpt.isEmpty() || resetTokenOpt.get().isExpired()) {
            model.addAttribute("message", "Invalid or expired token.");
            return "login";
        }

        User user = resetTokenOpt.get().getUser();
        localUserService.updatePassword(user, password);

        tokenService.deleteToken(resetTokenOpt.get());

        return "redirect:/login?resetSuccess";
    }

}