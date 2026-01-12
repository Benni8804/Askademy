package com.eduhub.controller;

import com.eduhub.dto.AnnouncementRequest;
import com.eduhub.dto.AnnouncementResponse;
import com.eduhub.model.Announcement;
import com.eduhub.model.Course;
import com.eduhub.model.User;
import com.eduhub.service.AnnouncementService;
import com.eduhub.service.CourseService;
import com.eduhub.service.NotificationService;
import com.eduhub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses/{courseId}/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByCourse(@PathVariable Integer courseId) {
        List<Announcement> announcements = announcementService.getAnnouncementsByCourseId(courseId);
        List<AnnouncementResponse> response = announcements.stream()
                .map(a -> new AnnouncementResponse(
                        a.getId(),
                        a.getTitle(),
                        a.getContent(),
                        a.getCreatedAt(),
                        a.getCourse().getId(),
                        a.getProfessor().getFirstname() + " " + a.getProfessor().getLastname()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PROFESSOR')")
    public ResponseEntity<?> createAnnouncement(
            @PathVariable Integer courseId,
            @Valid @RequestBody AnnouncementRequest request,
            Authentication authentication) {
        
        String email = authentication.getName();
        User professor = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Verify professor owns the course
        if (!course.getProfessor().getId().equals(professor.getId())) {
            return ResponseEntity.status(403).body("You are not the professor of this course");
        }

        Announcement announcement = new Announcement(
                request.getTitle(),
                request.getContent(),
                course,
                professor
        );

        Announcement saved = announcementService.createAnnouncement(announcement);

        // Notify all students in the course about the new announcement
        notificationService.notifyStudentsInCourse(course, 
            "New announcement: " + saved.getTitle());

        AnnouncementResponse response = new AnnouncementResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getCreatedAt(),
                saved.getCourse().getId(),
                professor.getFirstname() + " " + professor.getLastname()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('PROFESSOR')")
    public ResponseEntity<?> exportAnnouncementsToJson(
            @PathVariable Integer courseId,
            @RequestParam(defaultValue = "announcements_export.json") String filename,
            Authentication authentication) {
        
        try {
            announcementService.exportAnnouncementsToJson(filename);
            return ResponseEntity.ok(Map.of(
                "message", "Announcements exported successfully",
                "filename", filename
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to export announcements: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{announcementId}")
    @PreAuthorize("hasAuthority('PROFESSOR')")
    public ResponseEntity<?> deleteAnnouncement(
            @PathVariable Integer courseId,
            @PathVariable Long announcementId,
            Authentication authentication) {
        
        String email = authentication.getName();
        User professor = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Verify professor owns the course
        if (!course.getProfessor().getId().equals(professor.getId())) {
            return ResponseEntity.status(403).body("You are not the professor of this course");
        }

        announcementService.deleteAnnouncement(announcementId);
        return ResponseEntity.ok().build();
    }
}
