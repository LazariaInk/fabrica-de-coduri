package com.lazar.fabrica_de_coduri.model;

public class GuruLeaderboardEntry {

    private Long userId;
    private String username;

    private String mainStack;      // "Front-end", "Back-end", "Full-stack"
    private int frontendScore;    // nr de tehnologii front-end
    private int backendScore;     // nr de tehnologii back-end

    private int techCount;        // tehnologii distincte
    private String topTechnologies; // ex: "React, Spring, Docker"

    private int coursesCount;     // câte cursuri are
    private int completedCourses; // câte cursuri aproape/100% complete

    private int guruScore;        // scor final

    private String instagramLink;

    public GuruLeaderboardEntry() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMainStack() { return mainStack; }
    public void setMainStack(String mainStack) { this.mainStack = mainStack; }

    public int getFrontendScore() { return frontendScore; }
    public void setFrontendScore(int frontendScore) { this.frontendScore = frontendScore; }

    public int getBackendScore() { return backendScore; }
    public void setBackendScore(int backendScore) { this.backendScore = backendScore; }

    public int getTechCount() { return techCount; }
    public void setTechCount(int techCount) { this.techCount = techCount; }

    public String getTopTechnologies() { return topTechnologies; }
    public void setTopTechnologies(String topTechnologies) { this.topTechnologies = topTechnologies; }

    public int getCoursesCount() { return coursesCount; }
    public void setCoursesCount(int coursesCount) { this.coursesCount = coursesCount; }

    public int getCompletedCourses() { return completedCourses; }
    public void setCompletedCourses(int completedCourses) { this.completedCourses = completedCourses; }

    public int getGuruScore() { return guruScore; }
    public void setGuruScore(int guruScore) { this.guruScore = guruScore; }

    public String getInstagramLink() { return instagramLink; }
    public void setInstagramLink(String instagramLink) { this.instagramLink = instagramLink; }
}
