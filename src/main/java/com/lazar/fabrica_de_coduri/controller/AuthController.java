package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import com.lazar.fabrica_de_coduri.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Controller
public class AuthController {
    // Temporar dezactivat pentru deploy: login/reset conturi nu sunt disponibile public.
    private static final boolean AUTH_FEATURES_ENABLED = false;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          PasswordResetService passwordResetService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/login")
    public String loginPage() {
        if (!AUTH_FEATURES_ENABLED) {
            return "redirect:/";
        }

        return "login";
    }

    @GetMapping("/account/settings")
    public String accountSettings(Authentication authentication, Model model) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "account-settings";
    }

    @PostMapping("/account/settings/avatar")
    public String updateAvatar(Authentication authentication,
                               @RequestParam("avatar") MultipartFile avatar,
                               Model model) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String error = validateAvatar(avatar);
        if (error != null) {
            model.addAttribute("user", user);
            model.addAttribute("avatarError", error);
            return "account-settings";
        }

        try {
            Path uploadDir = Path.of("uploads", "avatars");
            Files.createDirectories(uploadDir);
            String extension = fileExtension(avatar.getOriginalFilename());
            String filename = user.getId() + "-" + UUID.randomUUID() + extension;
            avatar.transferTo(uploadDir.resolve(filename));
            user.setProfileImageUrl("/avatars/" + filename);
            userRepository.save(user);
            model.addAttribute("user", user);
            model.addAttribute("avatarSuccess", "Poza de profil a fost actualizata.");
        } catch (IOException e) {
            model.addAttribute("user", user);
            model.addAttribute("avatarError", "Nu am putut salva poza. Incearca din nou.");
        }

        return "account-settings";
    }

    @PostMapping("/account/settings/password")
    public String changePassword(Authentication authentication,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Parola curenta nu este corecta.");
            return "account-settings";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Parolele noi nu coincid.");
            return "account-settings";
        }

        if (newPassword.length() < 8) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Parola noua trebuie sa aiba cel putin 8 caractere.");
            return "account-settings";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        model.addAttribute("user", user);
        model.addAttribute("success", "Parola a fost schimbata.");
        return "account-settings";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        if (!AUTH_FEATURES_ENABLED) {
            return "redirect:/";
        }

        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String requestPasswordReset(@RequestParam String email,
                                       HttpServletRequest request,
                                       Model model) {
        if (!AUTH_FEATURES_ENABLED) {
            return "redirect:/";
        }

        String appUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
        passwordResetService.requestReset(email, appUrl);
        model.addAttribute("success", "Daca exista un cont cu acest email, ti-am trimis un link de resetare.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        if (!AUTH_FEATURES_ENABLED) {
            return "redirect:/";
        }

        String error = passwordResetService.validateToken(token);
        model.addAttribute("token", token);
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Model model) {
        if (!AUTH_FEATURES_ENABLED) {
            return "redirect:/";
        }

        String error = passwordResetService.resetPassword(token, newPassword, confirmPassword);
        if (error != null) {
            model.addAttribute("token", token);
            model.addAttribute("error", error);
            return "reset-password";
        }

        return "redirect:/login?reset";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    private String validateAvatar(MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) {
            return "Alege o poza inainte de salvare.";
        }
        if (avatar.getSize() > 5 * 1024 * 1024) {
            return "Poza trebuie sa aiba maximum 5MB.";
        }

        Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
        if (!allowedTypes.contains(avatar.getContentType())) {
            return "Format acceptat: JPG, PNG, WEBP sau GIF.";
        }

        return null;
    }

    private String fileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".png";
        }

        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
}
