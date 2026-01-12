package com.eduhub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.eduhub.model.Course;
import com.eduhub.model.User;

/**
 * Implementation of NotificationService interface.
 * This service handles sending notifications to users.
 * Currently logs notifications to console - can be extended to send emails, push notifications, etc.
 * 
 * Methods are @Async to demonstrate asynchronous processing with threads.
 * Thread names are logged to prove async execution in background threads.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    @Async
    public void sendNotification(User user, String message) {
        // Log async thread execution
        logger.debug("Async notification service running on thread: {}", Thread.currentThread().getName());
        
        // Simulate slow operation (e.g., connecting to email server)
        try {
            Thread.sleep(1000); // 1 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted while sending notification", e);
        }
        
        // Log notification - in production, this would send email/push notification
        logger.info("Notification sent to user {} ({}): {}", 
                    user.getEmail(), 
                    user.getFirstname() + " " + user.getLastname(), 
                    message);
        
        // TODO: Implement actual email/notification sending logic
        // Example: emailService.sendEmail(user.getEmail(), "Notification", message);
    }

    @Override
    @Async
    public void notifyStudentsInCourse(Course course, String message) {
        // Log async thread execution
        logger.debug("Async notification service running on thread: {} for course {}", 
                    Thread.currentThread().getName(), course.getName());
        
        // Simulate slow operation
        try {
            Thread.sleep(1000); // 1 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted while notifying students", e);
        }
        
        // Log notification for all students in course
        logger.info("Notification sent to all students in course '{}' (ID: {}): {}", 
                    course.getName(), 
                    course.getId(), 
                    message);
        
        // Notify each enrolled student
        course.getStudents().forEach(student -> {
            sendNotification(student, 
                String.format("Course '%s': %s", course.getName(), message));
        });
    }
}
