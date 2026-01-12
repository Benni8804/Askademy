package com.eduhub.repository;

import com.eduhub.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCourseIdOrderByCreatedAtDesc(Integer courseId);
    
    @Query("SELECT q FROM Question q WHERE q.course.id = :courseId AND SIZE(q.answers) = 0 ORDER BY q.createdAt DESC")
    List<Question> findUnansweredQuestionsByCourseId(Integer courseId);
    
    @Query("SELECT q FROM Question q WHERE q.course.id = :courseId AND SIZE(q.answers) > 0 ORDER BY q.createdAt DESC")
    List<Question> findAnsweredQuestionsByCourseId(Integer courseId);
}