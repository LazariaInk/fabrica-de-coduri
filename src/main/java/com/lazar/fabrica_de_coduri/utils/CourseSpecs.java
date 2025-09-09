package com.lazar.fabrica_de_coduri.utils;

import com.lazar.fabrica_de_coduri.model.*;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;

public class CourseSpecs {

    public static Specification<Course> isPublished() {
        return (root, cq, cb) -> cb.isTrue(root.get("published"));
    }

    public static Specification<Course> stackEquals(StackType stack) {
        return (root, cq, cb) -> cb.equal(root.get("stackType"), stack);
    }

    public static Specification<Course> languageEquals(ProgrammingLanguage lang) {
        return (root, cq, cb) -> cb.equal(root.get("programmingLanguage"), lang);
    }

    /** caută în title/description și numele tagurilor */
    public static Specification<Course> textMatches(String q) {
        return (root, cq, cb) -> {
            String like = "%" + q.toLowerCase() + "%";
            // left join tags (distinct in service)
            var tagsJoin = root.join("tags", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like),
                    cb.like(cb.lower(tagsJoin.get("name")), like)
            );
        };
    }
}
