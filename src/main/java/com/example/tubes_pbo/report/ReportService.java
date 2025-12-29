package com.example.tubes_pbo.report;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Service
public class ReportService implements ReportServiceInterface {

    @Override
    public File generateReport(Map<String, String> data) {
        try {
            File tmp = File.createTempFile("report-", ".txt");
            StringBuilder builder = new StringBuilder();
            data.forEach((k, v) -> builder.append(k).append(": ").append(v).append(System.lineSeparator()));
            Files.writeString(tmp.toPath(), builder.toString());
            return tmp;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate report", e);
        }
    }
}