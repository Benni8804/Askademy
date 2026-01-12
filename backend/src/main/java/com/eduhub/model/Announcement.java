package com.eduhub.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Announcement() {}

    public Announcement(String title, String content, Course course, User professor) {
        this.title = title;
        this.content = content;
        this.course = course;
        this.professor = professor;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public User getProfessor() { return professor; }
    public void setProfessor(User professor) { this.professor = professor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}