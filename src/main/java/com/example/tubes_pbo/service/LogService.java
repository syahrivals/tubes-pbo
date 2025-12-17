package com.example.tubes_pbo.service;

import com.example.tubes_pbo.model.ActivityLog;
import com.example.tubes_pbo.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final ActivityLogRepository logRepository;

    public LogService(ActivityLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void logActivity(String userRole, String username, String action, String entityType, String description) {
        ActivityLog log = new ActivityLog(userRole, username, action, entityType, description);
        logRepository.insert(log);
    }

    public void logNilaiCreated(String username, String nim, String mataKuliah) {
        logActivity("DOSEN", username, "CREATE", "NILAI", 
            "Menambah nilai untuk NIM " + nim + " mata kuliah " + mataKuliah);
    }

    public void logNilaiUpdated(String username, String nim, String mataKuliah) {
        logActivity("DOSEN", username, "UPDATE", "NILAI", 
            "Mengupdate nilai untuk NIM " + nim + " mata kuliah " + mataKuliah);
    }

    public void logNilaiDeleted(String username, String nim, String mataKuliah) {
        logActivity("DOSEN", username, "DELETE", "NILAI", 
            "Menghapus nilai untuk NIM " + nim + " mata kuliah " + mataKuliah);
    }

    public void logLogin(String userRole, String username) {
        logActivity(userRole, username, "LOGIN", "AUTH", "User login");
    }

    public void logLogout(String userRole, String username) {
        logActivity(userRole, username, "LOGOUT", "AUTH", "User logout");
    }

    public java.util.List<ActivityLog> findAll(int limit) {
        return logRepository.findAll(limit);
    }
}

