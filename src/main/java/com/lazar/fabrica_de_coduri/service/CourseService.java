package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.dto.CourseChapterFormDTO;
import com.lazar.fabrica_de_coduri.dto.CourseFormDTO;
import com.lazar.fabrica_de_coduri.dto.CourseLessonFormDTO;
import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.CourseChapter;
import com.lazar.fabrica_de_coduri.model.CourseLesson;
import com.lazar.fabrica_de_coduri.model.CourseTag;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;
import com.lazar.fabrica_de_coduri.repository.CourseTagRepository;
import com.lazar.fabrica_de_coduri.utils.SlugUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseTagRepository courseTagRepository;
    private final VideoStorageService storageService;


    public CourseService(CourseRepository courseRepository,
                         CourseTagRepository courseTagRepository,
                         VideoStorageService storageService) {
        this.courseRepository = courseRepository;
        this.courseTagRepository = courseTagRepository;
        this.storageService = storageService;
    }


    @Transactional
    public Course createCourseFromForm(CourseFormDTO form) {
        Course course = new Course();
        course.setTitle(form.getTitle());
        String courseSlug = ensureUniqueCourseSlug(SlugUtils.slugify(form.getTitle()));
        course.setSlug(courseSlug);
        course.setDescription(form.getDescription());
        course.setProgrammingLanguage(form.getProgrammingLanguage());
        course.setStackType(form.getStackType());
        course.setPrice(form.getPrice());             // dacă lipsea
        course.setCoverImageAlt(form.getCoverImageAlt());

        // COPERTĂ
        if (form.getCoverImage() != null && !form.getCoverImage().isEmpty()) {
            String coverPath = storageService.storeCourseCover(form.getCoverImage(), courseSlug);
            course.setCoverImagePath(coverPath);
        }

        // TAGS
        Set<CourseTag> tags = parseAndPersistTags(form.getTagsCsv());
        course.setTags(tags);

        // CHAPTERS + LESSONS (ca la tine)
        int chapIndex = 1;
        for (CourseChapterFormDTO chapDto : form.getChapters()) {
            CourseChapter chapter = new CourseChapter();
            chapter.setTitle(chapDto.getTitle());
            chapter.setSlug(SlugUtils.slugify(chapDto.getTitle()));
            chapter.setDescription(chapDto.getDescription());
            int pos = chapDto.getPosition() != null ? chapDto.getPosition() : chapIndex;
            chapter.setPosition(pos);

            int lessonIndex = 1;
            for (CourseLessonFormDTO lessonDto : chapDto.getLessons()) {
                CourseLesson lesson = new CourseLesson();
                lesson.setTitle(lessonDto.getTitle());
                lesson.setSlug(SlugUtils.slugify(lessonDto.getTitle()));
                int lpos = lessonDto.getPosition() != null ? lessonDto.getPosition() : lessonIndex;
                lesson.setPosition(lpos);

                String relPath = storageService.storeLessonVideo(
                        lessonDto.getVideoFile(), courseSlug, pos, lpos, lesson.getSlug());
                lesson.setVideoPath(relPath);

                chapter.addLesson(lesson);
                lessonIndex++;
            }
            course.addChapter(chapter);
            chapIndex++;
        }

        return courseRepository.save(course);
    }

    private String ensureUniqueCourseSlug(String base) {
        String slug = base;
        int i = 1;
        while (courseRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + (++i);
        }
        return slug;
    }

    private Set<CourseTag> parseAndPersistTags(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new LinkedHashSet<>();
        String[] parts = csv.split(",");
        Set<CourseTag> result = new LinkedHashSet<>();
        for (String p : parts) {
            String name = p.trim();
            if (name.isEmpty()) continue;
            String slug = SlugUtils.slugify(name);
            CourseTag tag = courseTagRepository.findBySlug(slug)
                    .orElseGet(() -> courseTagRepository.save(new CourseTag(name, slug)));
            result.add(tag);
        }
        return result;
    }
}