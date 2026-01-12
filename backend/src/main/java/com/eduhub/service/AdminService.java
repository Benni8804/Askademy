package com.eduhub.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduhub.repository.CourseRepository;
import com.eduhub.repository.QuestionRepository;
import com.eduhub.repository.UserRepository;
import com.eduhub.repository.AnswerRepository;
import com.eduhub.model.*;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private AnswerRepository answerRepository;

    public Map<String, Long> getSystemStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalQuestions", questionRepository.count());
        stats.put("totalAnswers", answerRepository.count());
        return stats;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }
}
