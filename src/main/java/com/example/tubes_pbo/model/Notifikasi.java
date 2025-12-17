package com.example.tubes_pbo.model;

import java.time.LocalDateTime;

public class Notifikasi {
    private Long id;
    private String nim;
    private String kodeDosen;
    private String pesan;
    private String tipe; // INFO, SUCCESS, WARNING, ERROR
    private boolean dibaca;
    private LocalDateTime createdAt;

    public Notifikasi() {
    }

    public Notifikasi(String nim, String pesan, String tipe) {
        this.nim = nim;
        this.pesan = pesan;
        this.tipe = tipe;
        this.dibaca = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notifikasi(String kodeDosen, String pesan, String tipe, boolean isDosen) {
        this.kodeDosen = kodeDosen;
        this.pesan = pesan;
        this.tipe = tipe;
        this.dibaca = false;
        this.createdAt = LocalDateTime.now();
    }

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

    public String getKodeDosen() {
        return kodeDosen;
    }

    public void setKodeDosen(String kodeDosen) {
        this.kodeDosen = kodeDosen;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public boolean isDibaca() {
        return dibaca;
    }

    public void setDibaca(boolean dibaca) {
        this.dibaca = dibaca;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

