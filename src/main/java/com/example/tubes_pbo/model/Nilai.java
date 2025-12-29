package com.example.tubes_pbo.model;

public class Nilai {
    private Long id;
    private Long enrollmentId;  // NEW: Relasi ke enrollment
    private Long mataKuliahId;   // NEW: Relasi ke mata kuliah
    private String nim;           // NEW: NIM mahasiswa
    private String mataKuliah;    // OLD: Backward compatibility
    private double tugas;
    private double uts;
    private double uas;

    public Nilai() {
    }

    // Constructor baru dengan enrollment
    public Nilai(Long enrollmentId, Long mataKuliahId, String nim, double tugas, double uts, double uas) {
        this.enrollmentId = enrollmentId;
        this.mataKuliahId = mataKuliahId;
        this.nim = nim;
        this.tugas = tugas;
        this.uts = uts;
        this.uas = uas;
    }

    // Old constructor untuk backward compatibility
    public Nilai(String mataKuliah, double tugas, double uts, double uas) {
        this.mataKuliah = mataKuliah;
        this.tugas = tugas;
        this.uts = uts;
        this.uas = uas;
    }

    public double hitungRataRata() {
        return (tugas + uts + uas) / 3.0;
    }

    public String getGrade() {
        double rata = hitungRataRata();
        if (rata >= 85) return "A";
        if (rata >= 75) return "B";
        if (rata >= 65) return "C";
        if (rata >= 55) return "D";
        return "E";
    }

    public String getMataKuliah() {
        return mataKuliah;
    }

    public void setMataKuliah(String mataKuliah) {
        this.mataKuliah = mataKuliah;
    }

    public double getTugas() {
        return tugas;
    }

    public void setTugas(double tugas) {
        this.tugas = tugas;
    }

    public double getUts() {
        return uts;
    }

    public void setUts(double uts) {
        this.uts = uts;
    }

    public double getUas() {
        return uas;
    }

    public void setUas(double uas) {
        this.uas = uas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Long getMataKuliahId() {
        return mataKuliahId;
    }

    public void setMataKuliahId(Long mataKuliahId) {
        this.mataKuliahId = mataKuliahId;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }
}

