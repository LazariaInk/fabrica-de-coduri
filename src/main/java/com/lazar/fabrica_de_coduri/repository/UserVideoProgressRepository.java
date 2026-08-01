package com.lazar.fabrica_de_coduri.repository;

import com.lazar.fabrica_de_coduri.model.CourseVideo;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.model.UserVideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, Long> {
    List<UserVideoProgress> findByUserAndVideoCourse(User user, com.lazar.fabrica_de_coduri.model.PremiumCourse course);

    Optional<UserVideoProgress> findByUserAndVideo(User user, CourseVideo video);
}
