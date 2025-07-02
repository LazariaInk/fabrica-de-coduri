package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.RegisterDTO;
import com.lazar.fabrica_de_coduri.service.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final RegisterService registrationService;

    public RegisterController(RegisterService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") RegisterDTO userDTO,
                                      Model model,
                                      HttpServletRequest request) {
        String appUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());

        String error = registrationService.registerUser(userDTO, appUrl);
        if (error != null) {
            model.addAttribute("error", error);
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/confirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        String error = registrationService.confirmUser(token);
        if (error != null) {
            model.addAttribute("message", error);
            return "confirm-error";
        }

        return "redirect:/login";
    }
}
