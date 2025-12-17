package com.example.tubes_pbo.report;

import java.io.File;
import java.util.Map;

public interface ReportServiceInterface {
    File generateReport(Map<String, String> data);
}

