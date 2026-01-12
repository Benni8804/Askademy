package com.eduhub.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, length = 8)
    private String courseCode;

    @Column
    private String description;

    @Column(length = 2000)
    private String gradingInfo;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @ManyToMany
    @JoinTable(name = "course_students",
               joinColumns = @JoinColumn(name = "course_id"),
               inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<User> students = new HashSet<>();

    @jakarta.persistence.OneToMany(mappedBy = "course", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<Question> questions = new java.util.ArrayList<>();

    @jakarta.persistence.OneToMany(mappedBy = "course", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<Announcement> announcements = new java.util.ArrayList<>();

    // Constructors
    public Course() {}

    public Course(String name, String description, User professor) {
        this.name = name;
        this.description = description;
        this.professor = professor;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getProfessor() { return professor; }
    public void setProfessor(User professor) { this.professor = professor; }

    public Set<User> getStudents() { return students; }
    public void setStudents(Set<User> students) { this.students = students; }

    public String getGradingInfo() { return gradingInfo; }
    public void setGradingInfo(String gradingInfo) { this.gradingInfo = gradingInfo; }

    public java.util.List<Question> getQuestions() { return questions; }
    public void setQuestions(java.util.List<Question> questions) { this.questions = questions; }

    public java.util.List<Announcement> getAnnouncements() { return announcements; }
    public void setAnnouncements(java.util.List<Announcement> announcements) { this.announcements = announcements; }
}