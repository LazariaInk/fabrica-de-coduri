package com.lazar.fabrica_de_coduri.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegacyRedirectController {

    @GetMapping("/lessons/**")
    public String redirectOldLessons(HttpServletRequest request) {
        String oldUri = request.getRequestURI(); // ex: /lessons/javascript/...
        String newUri = oldUri.replace("/lessons", "/lectii");
        return "redirect:" + newUri;
    }
}
