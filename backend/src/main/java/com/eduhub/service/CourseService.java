package com.eduhub.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduhub.exception.CourseNotFoundException;
import com.eduhub.exception.UnauthorizedActionException;
import com.eduhub.model.Course;
import com.eduhub.model.User;
import com.eduhub.repository.CourseRepository;
import com.eduhub.repository.UserRepository;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private final SecureRandom random = new SecureRandom();

    private String generateUniqueCourseCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (courseRepository.findByCourseCode(code).isPresent());
        return code;
    }

    public Course createCourse(Course course) {
        course.setCourseCode(generateUniqueCourseCode());
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Integer id) {
        return courseRepository.findById(Objects.requireNonNull(id));
    }

    public List<Course> getCoursesByProfessor(Integer professorId) {
        return courseRepository.findByProfessorId(professorId);
    }

    public List<Course> getCoursesByStudent(Integer studentId) {
        User student = userRepository.findById(Objects.requireNonNull(studentId))
                .orElseThrow(() -> new UnauthorizedActionException("Student not found"));
        return courseRepository.findAll().stream()
                .filter(course -> course.getStudents().contains(student))
                .toList();
    }

    public void enrollStudent(Integer courseId, Integer studentId) {
        Optional<Course> courseOpt = courseRepository.findById(Objects.requireNonNull(courseId));
        if (courseOpt.isEmpty()) {
            throw new CourseNotFoundException("Course not found");
        }
        Optional<User> studentOpt = userRepository.findById(Objects.requireNonNull(studentId));
        if (studentOpt.isEmpty()) {
            throw new UnauthorizedActionException("Student not found");
        }
        Course course = courseOpt.get();
        User student = studentOpt.get();
        if (student.getRole() != com.eduhub.model.Role.STUDENT) {
            throw new UnauthorizedActionException("Only students can enroll");
        }
        course.getStudents().add(student);
        courseRepository.save(course);
    }

    public void enrollStudentByCode(String courseCode, Integer studentId) {
        Optional<Course> courseOpt = courseRepository.findByCourseCode(courseCode);
        if (courseOpt.isEmpty()) {
            throw new CourseNotFoundException("Invalid course code");
        }
        Optional<User> studentOpt = userRepository.findById(Objects.requireNonNull(studentId));
        if (studentOpt.isEmpty()) {
            throw new UnauthorizedActionException("Student not found");
        }
        Course course = courseOpt.get();
        User student = studentOpt.get();
        if (student.getRole() != com.eduhub.model.Role.STUDENT) {
            throw new UnauthorizedActionException("Only students can enroll");
        }
        course.getStudents().add(student);
        courseRepository.save(course);
    }

    /**
     * Deletes a course if user has permission (course owner or ADMIN).
     * Related questions, answers, and announcements are deleted via cascade.
     */
    public void deleteCourse(Integer courseId, User currentUser) {
        Course course = courseRepository.findById(Objects.requireNonNull(courseId))
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        boolean isOwner = course.getProfessor().getId().equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equals(currentUser.getRole().name());

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedActionException("Only course owner or admin can delete this course");
        }

        courseRepository.delete(course);
    }
}