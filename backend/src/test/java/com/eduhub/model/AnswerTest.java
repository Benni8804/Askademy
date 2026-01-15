package com.eduhub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnswerTest {

    private Question question;
    private User student;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        User professor = new User(1, "Prof", "Smith", "prof@example.com", "pass", Role.PROFESSOR);
        Course course = new Course("Programming 3", "Java course", professor);
        student = new User(2, "John", "Doe", "student@example.com", "pass", Role.STUDENT);
        question = new Question("Question title", "Question content", student, course, false);
    }

    @Test
    void testDefaultConstructor() {
        Answer answer = new Answer();
        assertNotNull(answer, "Default constructor should create a non-null Answer object");
    }

    @Test
    void testParameterizedConstructor() {
        Answer answer = new Answer("This is my answer", student, question);
        
        assertNotNull(answer, "Parameterized constructor should create a non-null Answer object");
        assertEquals("This is my answer", answer.getContent(), "Content should match");
        assertEquals(student, answer.getAuthor(), "Author should match");
        assertEquals(question, answer.getQuestion(), "Question should match");
        assertFalse(answer.isVerified(), "Answer should not be verified by default");
    }

    @Test
    void testVerifiedAnswer() {
        Answer answer = new Answer("Verified answer", student, question);
        answer.setVerified(true);
        
        assertTrue(answer.isVerified(), "Answer should be marked as verified");
    }
}
