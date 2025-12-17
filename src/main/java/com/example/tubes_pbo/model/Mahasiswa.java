package com.example.tubes_pbo.model;

public class Mahasiswa extends Akun {
    private String nim;
    private String nama;

    public Mahasiswa() {
    }

    public Mahasiswa(String nim, String nama, String username, String password) {
        super(username, password);
        this.nim = nim;
        this.nama = nama;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}

