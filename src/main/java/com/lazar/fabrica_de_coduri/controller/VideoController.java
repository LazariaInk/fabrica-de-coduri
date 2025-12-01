package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.CourseLesson;
import com.lazar.fabrica_de_coduri.model.CourseOwnership;
import com.lazar.fabrica_de_coduri.repository.CourseLessonRepository;
import com.lazar.fabrica_de_coduri.repository.CourseOwnershipRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/secure-video")
public class VideoController {

    private final UserRepository userRepo;
    private final CourseLessonRepository lessonRepo;
    private final CourseOwnershipRepository ownershipRepo;

    // !!! schimbă în funcție de serverul tău
    private static final String VIDEO_BASE_PATH = "C:/Workspace/fabricadecoduri/src/main/resources/courses/";

    public VideoController(UserRepository userRepo,
                           CourseLessonRepository lessonRepo,
                           CourseOwnershipRepository ownershipRepo) {
        this.userRepo = userRepo;
        this.lessonRepo = lessonRepo;
        this.ownershipRepo = ownershipRepo;
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<ResourceRegion> streamLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestHeader HttpHeaders headers) throws IOException {

        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var user = userRepo.findByEmail(principal.getUsername())
                .orElseGet(() -> userRepo.findByUsername(principal.getUsername()).orElse(null));

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        CourseLesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var course = lesson.getChapter().getCourse();

        boolean hasCourse = ownershipRepo.existsByUserIdAndCourseIdAndStatus(
                user.getId(),
                course.getId(),
                CourseOwnership.Status.PAID
        );

        boolean isFreePreview = Boolean.TRUE.equals(lesson.getFreePreview());

        // dacă nu are cursul și nu e lecție de preview → blocăm
        if (!hasCourse && !isFreePreview) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (lesson.getVideoUrl() == null || lesson.getVideoUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video necunoscut");
        }

        File file = new File(VIDEO_BASE_PATH + lesson.getVideoUrl());
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fișier video lipsă");
        }

        Resource video = new FileSystemResource(file);
        ResourceRegion region = resourceRegion(video, headers);

        MediaType mediaType = MediaTypeFactory
                .getMediaType(video)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(mediaType)
                .body(region);
    }

    private ResourceRegion resourceRegion(Resource video, HttpHeaders headers) throws IOException {
        long contentLength = video.contentLength();
        List<HttpRange> ranges = headers.getRange();

        if (ranges == null || ranges.isEmpty()) {
            long rangeLength = Math.min(1024 * 1024, contentLength); // 1MB bucata
            return new ResourceRegion(video, 0, rangeLength);
        }

        HttpRange httpRange = ranges.get(0);
        long start = httpRange.getRangeStart(contentLength);
        long end = httpRange.getRangeEnd(contentLength);
        long rangeLength = Math.min(1024 * 1024, end - start + 1);

        return new ResourceRegion(video, start, rangeLength);
    }
}
