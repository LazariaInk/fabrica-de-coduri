package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "user_video_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_video_id"})
)
public class UserVideoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_video_id", nullable = false)
    private CourseVideo video;

    private int watchedSeconds;

    private boolean completed;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public CourseVideo getVideo() {
        return video;
    }

    public int getWatchedSeconds() {
        return watchedSeconds;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setVideo(CourseVideo video) {
        this.video = video;
    }

    public void setWatchedSeconds(int watchedSeconds) {
        this.watchedSeconds = watchedSeconds;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
