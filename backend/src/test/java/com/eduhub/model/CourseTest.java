package com.eduhub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseTest {

    private User professor;

    @BeforeEach
    void setUp() {
        professor = new User(1, "Prof", "Smith", "prof@example.com", "pass", Role.PROFESSOR);
    }

    @Test
    void testDefaultConstructor() {
        Course course = new Course();
        assertNotNull(course, "Default constructor should create a non-null Course object");
        assertNotNull(course.getStudents(), "Students set should be initialized");
    }

    @Test
    void testParameterizedConstructor() {
        Course course = new Course("Programming 3", "Learn Java and Spring Boot", professor);
        
        assertNotNull(course, "Parameterized constructor should create a non-null Course object");
        assertEquals("Programming 3", course.getName(), "Course name should match");
        assertEquals("Learn Java and Spring Boot", course.getDescription(), "Description should match");
        assertEquals(professor, course.getProfessor(), "Professor should match");
        assertNotNull(course.getStudents(), "Students set should be initialized");
        assertTrue(course.getStudents().isEmpty(), "Students set should be empty initially");
    }

    @Test
    void testAddStudent() {
        Course course = new Course("Math", "Mathematics course", professor);
        User student = new User(2, "John", "Doe", "student@example.com", "pass", Role.STUDENT);
        
        course.getStudents().add(student);
        
        assertEquals(1, course.getStudents().size(), "Students set should contain 1 student");
        assertTrue(course.getStudents().contains(student), "Students set should contain the added student");
    }

    @Test
    void testGradingInfo() {
        Course course = new Course("Physics", "Physics course", professor);
        String gradingInfo = "Midterm: 40%, Final: 60%";
        
        course.setGradingInfo(gradingInfo);
        
        assertEquals(gradingInfo, course.getGradingInfo(), "Grading info should match");
    }
}
