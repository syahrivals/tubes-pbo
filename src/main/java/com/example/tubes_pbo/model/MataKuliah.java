package com.example.tubes_pbo.model;

public class MataKuliah {
    private Long id;
    private String kode;
    private String nama;
    private int sks;
    private String kodeDosen;
    private String namaDosen;

    public MataKuliah() {
    }

    public MataKuliah(String kode, String nama, int sks, String kodeDosen) {
        this.kode = kode;
        this.nama = nama;
        this.sks = sks;
        this.kodeDosen = kodeDosen;
    }

    // Getters and Setters
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

    public int getSks() {
        return sks;
    }

    public void setSks(int sks) {
        this.sks = sks;
    }

    public String getKodeDosen() {
        return kodeDosen;
    }

    public void setKodeDosen(String kodeDosen) {
        this.kodeDosen = kodeDosen;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }
}

