package com.example.tubes_pbo.model;

public class Dosen extends Akun {
    private String kodeDosen;
    private String nama;

    public Dosen() {
    }

    public Dosen(String kodeDosen, String nama, String username, String password) {
        super(username, password);
        this.kodeDosen = kodeDosen;
        this.nama = nama;
    }

    public String getKodeDosen() {
        return kodeDosen;
    }

    public void setKodeDosen(String kodeDosen) {
        this.kodeDosen = kodeDosen;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}

