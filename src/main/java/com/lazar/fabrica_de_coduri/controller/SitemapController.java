package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.Lesson;
import com.lazar.fabrica_de_coduri.repository.LessonRepository;
import com.lazar.fabrica_de_coduri.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_XML_VALUE)
public class SitemapController {

    @Autowired
    private LessonRepository lessonRepository;

    private final String BASE_URL = "https://www.fabricadecoduri.com";

    @GetMapping("/sitemap.xml")
    public String generateSitemap() {
        List<Lesson> lessons = lessonRepository.findAll();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        xml.append(createUrl(BASE_URL + "/", "daily", "1.0"));

        xml.append(createUrl(BASE_URL + "/about", "yearly", "0.7"));

        for (Lesson lesson : lessons) {
            String topicSlug = SlugUtils.toSlug(lesson.getChapter().getTopic().getName());
            String chapterSlug = SlugUtils.toSlug(lesson.getChapter().getTitle());
            String lessonSlug = SlugUtils.toSlug(lesson.getTitle());

            String fullUrl = BASE_URL + "/lectii/" + topicSlug + "/" + chapterSlug + "/" + lessonSlug;
            xml.append(createUrl(fullUrl, "weekly", "0.8"));
        }

        xml.append("</urlset>");
        return xml.toString();
    }

    private String createUrl(String loc, String changefreq, String priority) {
        return new StringBuilder()
                .append("<url>")
                .append("<loc>").append(loc).append("</loc>")
                .append("<changefreq>").append(changefreq).append("</changefreq>")
                .append("<priority>").append(priority).append("</priority>")
                .append("</url>")
                .toString();
    }
}
