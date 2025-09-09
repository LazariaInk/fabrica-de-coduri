package com.lazar.fabrica_de_coduri.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class VideoStorageService {
    private final Path root;

    // opțional: limite pentru imagini (poți muta în config)
    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_IMAGE_EXT = new HashSet<>(Arrays.asList("jpg","jpeg","png","webp","avif"));

    public VideoStorageService(@Value("${video.storage.root}") String rootDir) {
        this.root = Paths.get(rootDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new RuntimeException("Nu pot crea directorul de stocare media: " + this.root, e);
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

        String filename = String.format("%02d-%s.%s", lessonPos, safeName(lessonSlug), ext);
        Path target = chapterDir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Eșec la salvarea fișierului video: " + target, e);
        }
        return "/media/" + folder + filename;
    }

    /**
     * Salvează coperta unui curs. Returnează calea publică relativă (ex: /media/courses/{slug}/cover.webp).
     * Convenție: /courses/{slug}/cover.{ext}
     */
    public String storeCourseCover(MultipartFile file, String courseSlug) {
        if (file == null || file.isEmpty()) return null;

        // 1) validări de bază
        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        boolean mimeOk = contentType.startsWith("image/");
        if (!mimeOk) {
            throw new IllegalArgumentException("Fișierul încărcat nu pare a fi o imagine (content-type: " + contentType + ")");
        }
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new IllegalArgumentException("Imagine prea mare. Limită: " + (MAX_IMAGE_BYTES / (1024*1024)) + "MB");
        }

        // 2) extensie
        String ext = getExtension(file.getOriginalFilename());
        if (ext == null || !ALLOWED_IMAGE_EXT.contains(ext.toLowerCase())) {
            // dacă vine cu extensie „ciudată”, încearcă să deduci din MIME
            ext = extFromMime(contentType).orElse("jpg");
        }

        // 3) directoare & nume
        String folder = "courses/" + courseSlug + "/";
        Path courseDir = root.resolve(folder);
        try {
            Files.createDirectories(courseDir);
        } catch (IOException e) {
            throw new RuntimeException("Nu pot crea folderul cursului: " + courseDir, e);
        }

        String filename = "cover." + ext.toLowerCase();
        Path target = courseDir.resolve(filename);

        // 4) copiere
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Eșec la salvarea copertei: " + target, e);
        }

        return "/media/" + folder + filename;
    }

    // ------- helpers -------

    private String getExtension(String original) {
        if (original == null) return null;
        String name = StringUtils.getFilename(original);
        if (name == null) return null;
        int dot = name.lastIndexOf('.');
        return dot != -1 ? name.substring(dot + 1).toLowerCase() : null;
    }

    private Optional<String> extFromMime(String mime) {
        if (mime == null) return Optional.empty();
        switch (mime) {
            case "image/jpeg": return Optional.of("jpg");
            case "image/png":  return Optional.of("png");
            case "image/webp": return Optional.of("webp");
            case "image/avif": return Optional.of("avif");
            default: return Optional.empty();
        }
    }

    /** mică igienizare pentru nume (evită spații, slash-uri etc.) */
    private String safeName(String s) {
        if (s == null) return "item";
        String n = s.toLowerCase()
                .replaceAll("[^a-z0-9\\-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("(^-|-$)", "");
        return n.isEmpty() ? "item" : n;
    }
}
