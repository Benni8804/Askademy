package com.eduhub.service;

import com.eduhub.model.User;
import com.eduhub.model.Course;

public interface NotificationService {

    void sendNotification(User user, String message);

    void notifyStudentsInCourse(Course course, String message);
}