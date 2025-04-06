package com.lazar.fabrica_de_coduri.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HtmlLessonController {

    @GetMapping("/lessons-html/{path}")
    public String serveLessonHtml(@PathVariable String path) {
        return "/lessons/" + path;
    }
}
