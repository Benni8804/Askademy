package com.eduhub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduhub.dto.QuestionGroupDto;
import com.eduhub.dto.QuestionRequest;
import com.eduhub.model.Question;
import com.eduhub.model.User;
import com.eduhub.service.QuestionService;

import jakarta.validation.Valid;

/**
 * REST Controller for Question management.
 * Delegates business logic to QuestionService.
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'STUDENT')")
    public ResponseEntity<List<Question>> getQuestionsByCourse(
            @PathVariable Integer courseId,
            @RequestParam(required = false) String filter) {
        
        List<Question> questions = questionService.getQuestionsByCourse(courseId, filter);
        return ResponseEntity.ok(questions);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'STUDENT')")
    public ResponseEntity<Question> createQuestion(
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal User user) {
        
        Question savedQuestion = questionService.createQuestion(request, user);
        return ResponseEntity.ok(savedQuestion);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'STUDENT')")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        questionService.deleteQuestion(id, user);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get questions grouped by semantic similarity.
     * Returns groups of similar questions with their similarity scores.
     * Only works in AI mode (app.ai.enabled=true with PostgreSQL + pgvector).
     * 
     * @param courseId The course ID to get grouped questions from
     * @param threshold Optional similarity threshold (0.0-1.0, default: 0.3)
     * @return List of question groups with similarity information
     */
    @GetMapping("/grouped/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'STUDENT')")
    public ResponseEntity<List<QuestionGroupDto>> getGroupedQuestions(
            @PathVariable Integer courseId,
            @RequestParam(required = false, defaultValue = "0.1") double threshold) {
        
        // Validate threshold range
        if (threshold < 0.0 || threshold > 1.0) {
            throw new IllegalArgumentException("Threshold must be between 0.0 and 1.0");
        }
        
        List<QuestionGroupDto> groups = questionService.getGroupedQuestions(courseId, threshold);
        return ResponseEntity.ok(groups);
    }
}
