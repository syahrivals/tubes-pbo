package com.example.tubes_pbo.model;

import java.time.LocalDateTime;

public class ActivityLog {
    private Long id;
    private String userRole; // DOSEN atau MAHASISWA
    private String username;
    private String action; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    private String entityType; // NILAI, MATA_KULIAH, MAHASISWA, ENROLLMENT
    private String entityId;
    private String description;
    private LocalDateTime timestamp;

    public ActivityLog() {
    }

    public ActivityLog(String userRole, String username, String action, String entityType, String description) {
        this.userRole = userRole;
        this.username = username;
        this.action = action;
        this.entityType = entityType;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

