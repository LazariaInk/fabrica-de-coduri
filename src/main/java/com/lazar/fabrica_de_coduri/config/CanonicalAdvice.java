package com.lazar.fabrica_de_coduri.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CanonicalAdvice {

    @ModelAttribute("canonicalUrl")
    public String injectCanonicalUrl(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }
}
