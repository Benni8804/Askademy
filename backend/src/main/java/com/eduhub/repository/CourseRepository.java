package com.eduhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduhub.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByProfessorId(Integer professorId);
    
    Optional<Course> findByCourseCode(String courseCode);
}