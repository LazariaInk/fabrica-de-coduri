package com.lazar.fabrica_de_coduri.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;


@Service
public class VideoStorageService {
    private final Path root;
    public VideoStorageService(@Value("${video.storage.root}") String rootDir) {
        this.root = Paths.get(rootDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new RuntimeException("Nu pot crea directorul de stocare video: " + this.root, e);
        }
    }
    /**
     * Returnează calea *relativă publică* (ex: /media/courses/...) salvată și în BD
     */
    public String storeLessonVideo(MultipartFile file, String courseSlug, int chapterPos, int lessonPos, String lessonSlug) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fișier video lipsă pentru lecție " + lessonSlug);
        }
        String ext = getExtension(file.getOriginalFilename());
        if (ext == null) ext = "mp4"; // default

        String folder = "courses/" + courseSlug + "/" + String.format("%02d", chapterPos) + "-" + "chapter" + "/";
        Path chapterDir = root.resolve(folder);
        try {
            Files.createDirectories(chapterDir);
        } catch (IOException e) {
            throw new RuntimeException("Nu pot crea folderul capitolului: " + chapterDir, e);
        }

        String filename = String.format("%02d-%s.%s", lessonPos, lessonSlug, ext);
        Path target = chapterDir.resolve(filename);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Eșec la salvarea fișierului video: " + target, e);
        }
        return "/media/" + folder + filename;
    }


    private String getExtension(String original) {
        if (original == null) return null;
        String name = StringUtils.getFilename(original);
        if (name == null) return null;
        int dot = name.lastIndexOf('.') ;
        return dot != -1 ? name.substring(dot + 1).toLowerCase() : null;
    }
}