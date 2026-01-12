package com.eduhub.service;

import com.eduhub.model.Announcement;
import com.eduhub.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementDataExporter exporter;

    public Announcement createAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    public List<Announcement> getAnnouncementsByCourseId(Integer courseId) {
        return announcementRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
    }

    public Optional<Announcement> getAnnouncementById(Long id) {
        return announcementRepository.findById(id);
    }

    public void exportAnnouncementsToJson(String filePath) throws Exception {
        List<Announcement> announcements = announcementRepository.findAll();
        exporter.exportToJson(announcements, filePath);
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}