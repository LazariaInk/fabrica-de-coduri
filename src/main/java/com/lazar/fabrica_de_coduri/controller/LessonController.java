package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Chapter;
import com.lazar.fabrica_de_coduri.model.Lesson;
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

    @GetMapping("/{topicSlug}/{chapterSlug}/{lessonSlug}")
    public String viewLessonFriendly(
            @PathVariable String topicSlug,
            @PathVariable String chapterSlug,
            @PathVariable String lessonSlug,
            Model model) {

        String topicSlugNorm = SlugUtils.toSlug(topicSlug);
        String chapterSlugNorm = SlugUtils.toSlug(chapterSlug);
        String lessonSlugNorm = SlugUtils.toSlug(lessonSlug);

        if (!topicSlug.equals(topicSlugNorm)
                || !chapterSlug.equals(chapterSlugNorm)
                || !lessonSlug.equals(lessonSlugNorm)) {

            String cleanUrl = "/lessons/" + topicSlugNorm + "/" + chapterSlugNorm + "/" + lessonSlugNorm;
            return "redirect:" + cleanUrl;
        }

        List<Lesson> allLessons = lessonRepository.findAll();

        for (Lesson lesson : allLessons) {
            String lessonSlugGenerated = SlugUtils.toSlug(lesson.getTitle());
            String chapterSlugGenerated = SlugUtils.toSlug(lesson.getChapter().getTitle());
            String topicSlugGenerated = SlugUtils.toSlug(lesson.getChapter().getTopic().getName());

            if (lessonSlugGenerated.equals(lessonSlugNorm)
                    && chapterSlugGenerated.equals(chapterSlugNorm)
                    && topicSlugGenerated.equals(topicSlugNorm)) {

                Topic topic = lesson.getChapter().getTopic();
                List<Chapter> chapters = topic.getChapters();

                model.addAttribute("platformInfo", platformInfoRepository.findById(1L).orElse(null));
                model.addAttribute("lesson", lesson);
                model.addAttribute("chapters", chapters);
                model.addAttribute("topics", topicRepository.findAll());

                return lesson.getHtmlPath();
            }
        }

        throw new RuntimeException("Lesson not found");
    }

}
