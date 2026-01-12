package com.eduhub.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduhub.service.AdminService;
import com.eduhub.model.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Long>> getSystemStats() {
        return ResponseEntity.ok(adminService.getSystemStats());
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/courses")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    @GetMapping("/questions")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(adminService.getAllQuestions());
    }

    @GetMapping("/answers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Answer>> getAllAnswers() {
        return ResponseEntity.ok(adminService.getAllAnswers());
    }
}
