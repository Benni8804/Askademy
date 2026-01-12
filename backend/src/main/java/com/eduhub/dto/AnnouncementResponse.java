package com.eduhub.dto;

import java.time.LocalDateTime;

public class AnnouncementResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Integer courseId;
    private String professorName;

    public AnnouncementResponse() {}

    public AnnouncementResponse(Long id, String title, String content, LocalDateTime createdAt, 
                                Integer courseId, String professorName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.courseId = courseId;
        this.professorName = professorName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getProfessorName() { return professorName; }
    public void setProfessorName(String professorName) { this.professorName = professorName; }
}
