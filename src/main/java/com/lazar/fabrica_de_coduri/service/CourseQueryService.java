
package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.dto.CourseCardDTO;
import com.lazar.fabrica_de_coduri.dto.CoursesPageDTO;
import com.lazar.fabrica_de_coduri.model.*;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;

import com.lazar.fabrica_de_coduri.utils.CourseSpecs;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseQueryService {

    private final CourseRepository courseRepository;

    public CourseQueryService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public CoursesPageDTO search(String q, String stack, String lang, int page, int size) {
        page = Math.max(1, page);
        size = Math.max(1, Math.min(50, size));

        Specification<Course> spec = Specification.where(CourseSpecs.isPublished());

        if (q != null && !q.isBlank()) {
            spec = spec.and(CourseSpecs.textMatches(q.trim()));
        }
        if (stack != null && !stack.isBlank()) {
            spec = spec.and(CourseSpecs.stackEquals(StackType.valueOf(stack)));
        }
        if (lang != null && !lang.isBlank()) {
            spec = spec.and(CourseSpecs.languageEquals(ProgrammingLanguage.valueOf(lang)));
        }

        // sort: cele mai recent update first, apoi titlul
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updatedAt").and(Sort.by("title")));
        Page<Course> result = courseRepository.findAll(spec, pageable);

        // pregătim tags pentru fiecare (evităm N+1: @ManyToMany LAZY, dar JPA va face batch; e ok pt. listă)
        List<CourseCardDTO> items = result.getContent().stream().map(this::toCard).collect(Collectors.toList());

        return new CoursesPageDTO(
                items,
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private CourseCardDTO toCard(Course c) {
        CourseCardDTO dto = new CourseCardDTO(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getStackType().name(),
                c.getProgrammingLanguage().name(),
                c.getDescription(),
                c.getCoverImagePath() != null ? c.getCoverImagePath()
                        : "https://picsum.photos/seed/fdc-" + c.getSlug() + "/640/360",
                c.getTags().stream().map(t -> t.getName()).limit(10).toList(),
                "/courses/" + c.getSlug(),
                c.getUpdatedAt() != null ? c.getUpdatedAt().toString().substring(0, 10) : null
        );

        // ✅ PREȚ (RON)
        dto.price = c.getPrice();     // BigDecimal scale=2, exact ce ai în DB
        dto.currency = "RON";

        // ✅ CAPITOLE + LECȚII (cum aveai)
        dto.chapters = c.getChapters().stream()
                .sorted(Comparator.comparingInt(ch -> ch.getPosition()))
                .map(ch -> {
                    CourseCardDTO.ChapterItem ci = new CourseCardDTO.ChapterItem();
                    ci.position = ch.getPosition();
                    ci.title = ch.getTitle();
                    ci.description = ch.getDescription();
                    ci.lessons = ch.getLessons().stream()
                            .sorted(Comparator.comparingInt(ls -> ls.getPosition()))
                            .map(ls -> {
                                CourseCardDTO.LessonItem li = new CourseCardDTO.LessonItem();
                                li.position = ls.getPosition();
                                li.title = ls.getTitle();
                                return li;
                            }).collect(Collectors.toList());
                    return ci;
                }).collect(Collectors.toList());

        return dto;
    }

}
