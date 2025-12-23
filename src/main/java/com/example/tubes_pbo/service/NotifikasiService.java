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

    public void createNotifikasiDosen(String pesan, String tipe) {
        Notifikasi notifikasi = new Notifikasi(null, pesan, tipe); // nim = null untuk dosen
        notifikasiRepository.insert(notifikasi);
    }

    public void notifyEnrollmentPending(String kodeDosen, String namaMahasiswa, String mataKuliah) {
        createNotifikasiDosen(
            "Mahasiswa " + namaMahasiswa + " meminta enrollment untuk mata kuliah " + mataKuliah,
            "INFO"
        );
    }

    public void notifyEnrollment(String nim, String mataKuliah) {
        createNotifikasi(nim,
            "Anda telah mengirim permintaan enrollment untuk mata kuliah " + mataKuliah,
            "INFO");
    }
}

