package com.example.tubes_pbo.service;

import org.springframework.stereotype.Service;

import com.example.tubes_pbo.model.Notifikasi;
import com.example.tubes_pbo.repository.NotifikasiRepository;

@Service
public class NotifikasiService {

    private final NotifikasiRepository notifikasiRepository;

    public NotifikasiService(NotifikasiRepository notifikasiRepository) {
        this.notifikasiRepository = notifikasiRepository;
    }

    public void createNotifikasi(String nim, String pesan, String tipe) {
        Notifikasi notifikasi = new Notifikasi(nim, pesan, tipe);
        notifikasiRepository.insert(notifikasi);
    }

    public void createNotifikasiDosen(String kodeDosen, String pesan, String tipe) {
        Notifikasi notifikasi = new Notifikasi(kodeDosen, pesan, tipe, true);
        notifikasiRepository.insert(notifikasi);
    }

    public void notifyNilaiBaru(String nim, String mataKuliah) {
        createNotifikasi(nim, 
            "Nilai baru untuk mata kuliah " + mataKuliah + " telah ditambahkan!", 
            "INFO");
    }

    public void notifyNilaiDiupdate(String nim, String mataKuliah) {
        createNotifikasi(nim, 
            "Nilai mata kuliah " + mataKuliah + " telah diupdate!", 
            "SUCCESS");
    }

    public void notifyEnrollment(String nim, String mataKuliah) {
        createNotifikasi(nim, 
            "Anda berhasil mendaftar mata kuliah " + mataKuliah + "!", 
            "SUCCESS");
    }

    public void notifyEnrollmentRequest(String nim, String mataKuliah) {
        createNotifikasi(nim, 
            "Permintaan pendaftaran mata kuliah " + mataKuliah + " telah dikirim ke dosen untuk disetujui!", 
            "INFO");
    }

    public void notifyEnrollmentApproved(String nim, String mataKuliah) {
        createNotifikasi(nim, 
            "Permintaan pendaftaran mata kuliah " + mataKuliah + " telah DISETUJUI dosen!", 
            "SUCCESS");
    }

    public void notifyEnrollmentRejected(String nim, String mataKuliah) {
        createNotifikasi(nim, 
            "Permintaan pendaftaran mata kuliah " + mataKuliah + " telah DITOLAK dosen!", 
            "WARNING");
    }

    public void notifyNewEnrollmentRequest(String kodeDosen, String namaMahasiswa, String nim, String mataKuliah) {
        createNotifikasiDosen(kodeDosen, 
            "Mahasiswa " + namaMahasiswa + " (NIM: " + nim + ") mengajukan pendaftaran mata kuliah " + mataKuliah, 
            "INFO");
    }
}

