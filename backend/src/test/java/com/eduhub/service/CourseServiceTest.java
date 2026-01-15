package com.eduhub.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eduhub.model.Course;
import com.eduhub.model.Role;
import com.eduhub.model.User;
import com.eduhub.repository.CourseRepository;
import com.eduhub.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    private User professor;
    private User student;
    private Course course;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        professor = new User(1, "Prof", "Smith", "prof@example.com", "pass", Role.PROFESSOR);
        student = new User(2, "John", "Doe", "student@example.com", "pass", Role.STUDENT);
        course = new Course("Programming 3", "Learn Java and Spring Boot", professor);
        course.setId(1);
    }

    @Test
    void testCreateCourse() {
        Course newCourse = new Course("Programming 3", "Learn Java and Spring Boot", professor);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course created = courseService.createCourse(newCourse);

        assertNotNull(created);
        assertEquals(1, created.getId());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testGetAllCourses() {
        List<Course> courses = Arrays.asList(course);
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getAllCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Programming 3", result.get(0).getName());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testGetCourseById_Success() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        Optional<Course> found = courseService.getCourseById(1);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().getId());
        assertEquals("Programming 3", found.get().getName());
        verify(courseRepository, times(1)).findById(1);
    }

    @Test
    void testGetCourseById_NotFound() {
        when(courseRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Course> found = courseService.getCourseById(999);

        assertFalse(found.isPresent());
        verify(courseRepository, times(1)).findById(999);
    }

    @Test
    void testEnrollStudent() {
        student.setId(2);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(userRepository.findById(2)).thenReturn(Optional.of(student));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        courseService.enrollStudent(1, 2);

        assertTrue(course.getStudents().contains(student));
        verify(courseRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(2);
        verify(courseRepository, times(1)).save(course);
    }
}
