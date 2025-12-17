package com.example.tubes_pbo.model;

public class Semester {
    private Long id;
    private String kode; // Contoh: "2024-1" (tahun-semester)
    private String nama; // Contoh: "Semester Ganjil 2024/2025"
    private boolean aktif;

    public Semester() {
    }

    public Semester(String kode, String nama, boolean aktif) {
        this.kode = kode;
        this.nama = nama;
        this.aktif = aktif;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }
}

