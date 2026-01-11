package com.lazar.fabrica_de_coduri.config;

import com.lazar.fabrica_de_coduri.repository.ChapterRepository;
import com.lazar.fabrica_de_coduri.repository.LessonRepository;
import com.lazar.fabrica_de_coduri.repository.TopicRepository;
import com.lazar.fabrica_de_coduri.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SlugBackfillRunner implements CommandLineRunner {

    @Autowired
    TopicRepository topicRepo;
    @Autowired
    ChapterRepository chapterRepo;
    @Autowired
    LessonRepository lessonRepo;

    @Override
    public void run(String... args) {
        topicRepo.findAll().forEach(t -> { t.setSlug(SlugUtils.toSlug(t.getName())); });
        topicRepo.saveAll(topicRepo.findAll());

        chapterRepo.findAll().forEach(c -> { c.setSlug(SlugUtils.toSlug(c.getTitle())); });
        chapterRepo.saveAll(chapterRepo.findAll());

        lessonRepo.findAll().forEach(l -> { l.setSlug(SlugUtils.toSlug(l.getTitle())); });
        lessonRepo.saveAll(lessonRepo.findAll());
    }
}

