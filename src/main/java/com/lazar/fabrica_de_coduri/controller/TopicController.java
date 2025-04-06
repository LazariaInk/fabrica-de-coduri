package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Chapter;
import com.lazar.fabrica_de_coduri.model.Lesson;
import com.lazar.fabrica_de_coduri.model.Topic;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @GetMapping
    public String showTopics(Model model) {
        model.addAttribute("topics", topicRepository.findAll());
        return "topics";
    }

    @GetMapping("/{topicId}")
    public String showFirstLesson(@PathVariable Long topicId, Model model) {
        Topic topic = topicRepository.findById(topicId).orElseThrow();
        Chapter firstChapter = topic.getChapters().get(0);
        Lesson firstLesson = firstChapter.getLessons().get(0);

        return "redirect:/lessons/" + firstLesson.getId();
    }
}
