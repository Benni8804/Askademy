package com.eduhub.service;

import java.io.IOException;
import java.util.List;

public interface DataExporter<T> {

    void exportToJson(List<T> data, String filePath) throws IOException;

    List<T> importFromJson(String filePath) throws IOException;
}