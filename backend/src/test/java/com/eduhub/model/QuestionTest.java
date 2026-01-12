package com.eduhub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionTest {

    private Course course;
    private User student;

    @BeforeEach
    void setUp() {
        User professor = new User(1, "Prof", "Smith", "prof@example.com", "pass", Role.PROFESSOR);
        course = new Course("Programming 3", "Java course", professor);
        student = new User(2, "John", "Doe", "student@example.com", "pass", Role.STUDENT);
    }

    @Test
    void testDefaultConstructor() {
        Question question = new Question();
        assertNotNull(question, "Default constructor should create a non-null Question object");
    }

    @Test
    void testParameterizedConstructor() {
        Question question = new Question("How to use Spring Boot?", "I need help with Spring Boot configuration", student, course, false);
        
        assertNotNull(question, "Parameterized constructor should create a non-null Question object");
        assertEquals("How to use Spring Boot?", question.getTitle(), "Title should match");
        assertEquals("I need help with Spring Boot configuration", question.getContent(), "Content should match");
        assertEquals(student, question.getAuthor(), "Author should match");
        assertEquals(course, question.getCourse(), "Course should match");
        assertFalse(question.isAnonymous(), "Question should not be anonymous by default");
    }

    @Test
    void testAnonymousQuestion() {
        Question question = new Question("Anonymous question", "Content", student, course, true);
        
        assertTrue(question.isAnonymous(), "Question should be marked as anonymous");
    }
}
