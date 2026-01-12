package com.eduhub.service;

import com.eduhub.model.Announcement;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class AnnouncementDataExporter implements DataExporter<Announcement> {

    private final ObjectMapper objectMapper;

    public AnnouncementDataExporter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void exportToJson(List<Announcement> data, String filePath) throws IOException {
        objectMapper.writeValue(new File(filePath), data);
    }

    @Override
    public List<Announcement> importFromJson(String filePath) throws IOException {
        return objectMapper.readValue(new File(filePath), new TypeReference<List<Announcement>>() {});
    }
}