package com.example.tubes_pbo.model;

import java.time.LocalDateTime;

public class Enrollment {
    private Long id;
    private String nim;
    private Long mataKuliahId;
    private String status; // ENROLLED, COMPLETED, DROPPED
    private LocalDateTime enrolledAt;
    
    // For display purposes (joined from other tables)
    private String namaMahasiswa;
    private String kodeMataKuliah;
    private String namaMataKuliah;
    private int sks;

    public Enrollment() {
    }

    public Enrollment(String nim, Long mataKuliahId, String status) {
        this.nim = nim;
        this.mataKuliahId = mataKuliahId;
        this.status = status;
        this.enrolledAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public Long getMataKuliahId() {
        return mataKuliahId;
    }

    public void setMataKuliahId(Long mataKuliahId) {
        this.mataKuliahId = mataKuliahId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }

    public String getKodeMataKuliah() {
        return kodeMataKuliah;
    }

    public void setKodeMataKuliah(String kodeMataKuliah) {
        this.kodeMataKuliah = kodeMataKuliah;
    }

    public String getNamaMataKuliah() {
        return namaMataKuliah;
    }

    public void setNamaMataKuliah(String namaMataKuliah) {
        this.namaMataKuliah = namaMataKuliah;
    }

    public int getSks() {
        return sks;
    }

    public void setSks(int sks) {
        this.sks = sks;
    }
}

