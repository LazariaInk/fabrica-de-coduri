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
@RequestMapping("/lectii")
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
            String cleanUrl = "/lectii/" + topicSlugNorm + "/" + chapterSlugNorm + "/" + lessonSlugNorm;
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

                List<Lesson> topicLessons = topic.getChapters()
                        .stream()
                        .sorted((c1, c2) -> Integer.compare(c1.getOrderNumber(), c2.getOrderNumber()))
                        .flatMap(chapter -> chapter.getLessons()
                                .stream()
                                .sorted((l1, l2) -> Integer.compare(l1.getOrderNumber(), l2.getOrderNumber())))
                        .toList();

                int currentIndex = -1;
                for (int i = 0; i < topicLessons.size(); i++) {
                    if (topicLessons.get(i).getId().equals(lesson.getId())) {
                        currentIndex = i;
                        break;
                    }
                }

                Lesson previousLesson = (currentIndex > 0) ? topicLessons.get(currentIndex - 1) : null;
                Lesson nextLesson = (currentIndex < topicLessons.size() - 1) ? topicLessons.get(currentIndex + 1) : null;

                model.addAttribute("platformInfo", platformInfoRepository.findById(1L).orElse(null));
                model.addAttribute("lesson", lesson);
                model.addAttribute("chapters", topic.getChapters());
                model.addAttribute("topics", topicRepository.findAll());
                model.addAttribute("previousLesson", previousLesson);
                model.addAttribute("nextLesson", nextLesson);
                model.addAttribute("activeTopicId", topic.getId());
                model.addAttribute("activeLessonId", lesson.getId());
                model.addAttribute("activeChapterId", lesson.getChapter().getId());

                return lesson.getHtmlPath();
            }
        }

        throw new RuntimeException("Lesson not found");
    }


    @GetMapping("/topic/{topicId}")
    public String redirectToFirstLesson(@PathVariable Long topicId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new RuntimeException("Topic not found"));
        List<Chapter> chapters = topic.getChapters();

        if (chapters.isEmpty()) {
            throw new RuntimeException("No chapters found for topic");
        }

        Chapter firstChapter = chapters.stream()
                .sorted((c1, c2) -> c1.getId().compareTo(c2.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No chapters found"));

        List<Lesson> lessons = firstChapter.getLessons();

        if (lessons.isEmpty()) {
            throw new RuntimeException("No lessons found in first chapter");
        }

        Lesson firstLesson = lessons.stream()
                .sorted((l1, l2) -> l1.getId().compareTo(l2.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No lessons found"));

        String topicSlug = SlugUtils.toSlug(topic.getName());
        String chapterSlug = SlugUtils.toSlug(firstChapter.getTitle());
        String lessonSlug = SlugUtils.toSlug(firstLesson.getTitle());

        return "redirect:/lectii/" + topicSlug + "/" + chapterSlug + "/" + lessonSlug;
    }


}
