package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.PremiumCourse;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.model.UserCourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCourseProgressRepository extends JpaRepository<UserCourseProgress, Long> {
    List<UserCourseProgress> findByUser(User user);

    Optional<UserCourseProgress> findByUserAndCourse(User user, PremiumCourse course);
}
