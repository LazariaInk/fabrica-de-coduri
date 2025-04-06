package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Chapter;
import com.lazar.fabrica_de_coduri.model.Lesson;
import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.model.Topic;
import com.lazar.fabrica_de_coduri.repository.ChapterRepository;
import com.lazar.fabrica_de_coduri.repository.LessonRepository;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/lessons")
public class LessonController {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PlatformInfoRepository platformInfoRepository;

    @GetMapping("/{lessonId}")
    public String viewLesson(@PathVariable Long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();

        Topic topic = lesson.getChapter().getTopic();
        List<Chapter> chapters = topic.getChapters();
        model.addAttribute("topics", topicRepository.findAll());

        PlatformInfo platformInfo = platformInfoRepository.findById(1L).orElse(null);
        model.addAttribute("platformInfo", platformInfo);
        model.addAttribute("lesson", lesson);
        model.addAttribute("chapters", chapters);
        model.addAttribute("topics", topicRepository.findAll());
        return lesson.getHtmlPath();
    }

    @GetMapping("/{topicSlug}/{chapterSlug}/{lessonSlug}")
    public String viewLessonFriendly(
            @PathVariable String topicSlug,
            @PathVariable String chapterSlug,
            @PathVariable String lessonSlug,
            Model model) {

        // Normalizează slug-urile primite din URL
        String topicSlugNorm = SlugUtils.toSlug(topicSlug);
        String chapterSlugNorm = SlugUtils.toSlug(chapterSlug);
        String lessonSlugNorm = SlugUtils.toSlug(lessonSlug);

        System.out.println("=== Incoming Slugs (NORMALIZED) ===");
        System.out.println("Topic slug: " + topicSlugNorm);
        System.out.println("Chapter slug: " + chapterSlugNorm);
        System.out.println("Lesson slug: " + lessonSlugNorm);
        System.out.println("===================================");

        List<Lesson> allLessons = lessonRepository.findAll();

        for (Lesson lesson : allLessons) {
            String lessonSlugGenerated = SlugUtils.toSlug(lesson.getTitle());
            String chapterSlugGenerated = SlugUtils.toSlug(lesson.getChapter().getTitle());
            String topicSlugGenerated = SlugUtils.toSlug(lesson.getChapter().getTopic().getName());

            System.out.println(">> Checking lesson:");
            System.out.println("  Lesson title: " + lesson.getTitle() + " -> " + lessonSlugGenerated);
            System.out.println("  Chapter title: " + lesson.getChapter().getTitle() + " -> " + chapterSlugGenerated);
            System.out.println("  Topic name: " + lesson.getChapter().getTopic().getName() + " -> " + topicSlugGenerated);

            if (lessonSlugGenerated.equals(lessonSlugNorm)
                    && chapterSlugGenerated.equals(chapterSlugNorm)
                    && topicSlugGenerated.equals(topicSlugNorm)) {

                System.out.println(">>> MATCH FOUND! Lesson ID: " + lesson.getId());

                Topic topic = lesson.getChapter().getTopic();
                List<Chapter> chapters = topic.getChapters();

                model.addAttribute("platformInfo", platformInfoRepository.findById(1L).orElse(null));
                model.addAttribute("lesson", lesson);
                model.addAttribute("chapters", chapters);
                model.addAttribute("topics", topicRepository.findAll());

                return lesson.getHtmlPath();
            }
        }

        System.out.println("!!! No matching lesson found for slugs.");
        throw new RuntimeException("Lesson not found");
    }



}
