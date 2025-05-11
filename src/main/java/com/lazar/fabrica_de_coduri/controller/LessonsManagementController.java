package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Chapter;
import com.lazar.fabrica_de_coduri.model.Lesson;
import com.lazar.fabrica_de_coduri.model.Topic;
import com.lazar.fabrica_de_coduri.repository.ChapterRepository;
import com.lazar.fabrica_de_coduri.repository.LessonRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
public class LessonsManagementController {
    @Autowired
    private TopicRepository topicRepo;

    @Autowired
    private ChapterRepository chapterRepo;

    @Autowired
    private LessonRepository lessonRepo;

    @GetMapping("/admin/lessons-management")
    public String lessonForm(Model model) {
        model.addAttribute("lesson", new Lesson());
        model.addAttribute("topics", topicRepo.findAll());

        List<Chapter> sortedChapters = chapterRepo.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Chapter::getOrderNumber))
                .toList();
        model.addAttribute("chapters", sortedChapters);

        List<Lesson> sortedLessons = lessonRepo.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Lesson::getOrderNumber))
                .toList();
        model.addAttribute("lessons", sortedLessons);

        return "lessons-management";
    }

    @PostMapping("/admin/lessons/add")
    public String addLesson(@ModelAttribute Lesson lesson,
                            @RequestParam Long chapterId) {
        Chapter chapter = chapterRepo.findById(chapterId).orElseThrow();
        lesson.setChapter(chapter);
        lessonRepo.save(lesson);
        return "redirect:/admin/lessons-management";
    }

    @PostMapping("/admin/lessons/update")
    public String updateLesson(@ModelAttribute Lesson lesson,
                               @RequestParam Long chapterId,
                               @RequestParam Integer orderNumber) {
        Chapter chapter = chapterRepo.findById(chapterId).orElseThrow();
        lesson.setChapter(chapter);
        lesson.setOrderNumber(orderNumber);
        lessonRepo.save(lesson);
        return "redirect:/admin/lessons-management";
    }

    @PostMapping("/admin/topics/add")
    public String addTopic(@RequestParam String name,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) String codeSnippet) {
        Topic topic = new Topic(name);
        topic.setDescription(description);
        topic.setCodeSnippet(codeSnippet);
        topicRepo.save(topic);
        return "redirect:/admin/lessons-management";
    }

    @PostMapping("/admin/topics/update")
    public String updateTopic(@RequestParam Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) String codeSnippet) {
        Topic topic = topicRepo.findById(id).orElseThrow();
        topic.setName(name);
        topic.setDescription(description);
        topic.setCodeSnippet(codeSnippet);
        topicRepo.save(topic);
        return "redirect:/admin/lessons-management";
    }

    @PostMapping("/admin/chapters/add")
    public String addChapter(@RequestParam String title,
                             @RequestParam Long topicId,
                             @RequestParam Integer orderNumber) {
        Topic topic = topicRepo.findById(topicId).orElseThrow();
        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setTopic(topic);
        chapter.setOrderNumber(orderNumber);
        chapterRepo.save(chapter);
        return "redirect:/admin/lessons-management";
    }

    @PostMapping("/admin/chapters/update")
    public String updateChapter(@RequestParam Long id,
                                @RequestParam String title,
                                @RequestParam Long topicId,
                                @RequestParam Integer orderNumber) {
        Chapter chapter = chapterRepo.findById(id).orElseThrow();
        Topic topic = topicRepo.findById(topicId).orElseThrow();
        chapter.setTitle(title);
        chapter.setTopic(topic);
        chapter.setOrderNumber(orderNumber);
        chapterRepo.save(chapter);
        return "redirect:/admin/lessons-management";
    }

    @GetMapping("/admin/topics/delete/{id}")
    public String deleteTopic(@PathVariable Long id) {
        topicRepo.deleteById(id);
        return "redirect:/admin/lessons-management";
    }

    @GetMapping("/admin/chapters/delete/{id}")
    public String deleteChapter(@PathVariable Long id) {
        chapterRepo.deleteById(id);
        return "redirect:/admin/lessons-management";
    }

    @GetMapping("/admin/lessons/delete/{id}")
    public String deleteLesson(@PathVariable Long id) {
        lessonRepo.deleteById(id);
        return "redirect:/admin/lessons-management";
    }
}
