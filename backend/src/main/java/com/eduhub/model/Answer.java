package com.eduhub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private Question question;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean anonymous = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Answer() {}

    public Answer(String content, User author, Question question) {
        this.content = content;
        this.author = author;
        this.question = question;
        this.createdAt = LocalDateTime.now();
        this.anonymous = false;
    }

    public Answer(String content, User author, Question question, boolean anonymous) {
        this.content = content;
        this.author = author;
        this.question = question;
        this.createdAt = LocalDateTime.now();
        this.anonymous = anonymous;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}