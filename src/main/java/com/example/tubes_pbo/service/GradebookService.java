package com.example.tubes_pbo.service;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.example.tubes_pbo.model.Mahasiswa;
import com.example.tubes_pbo.model.Nilai;
import com.example.tubes_pbo.report.ReportServiceInterface;
import com.example.tubes_pbo.repository.MahasiswaRepository;
import com.example.tubes_pbo.repository.NilaiRepository;

@Service
public class GradebookService {

    private final MahasiswaRepository mahasiswaRepository;
    private final NilaiRepository nilaiRepository;
    private final ReportServiceInterface reportService;
    private final PasswordService passwordService;

    public GradebookService(MahasiswaRepository mahasiswaRepository,
                            NilaiRepository nilaiRepository,
                            ReportServiceInterface reportService,
                            PasswordService passwordService) {
        this.mahasiswaRepository = mahasiswaRepository;
        this.nilaiRepository = nilaiRepository;
        this.reportService = reportService;
        this.passwordService = passwordService;
    }

    @PostConstruct
    public void seedData() {
        if (mahasiswaRepository.count() == 0) {
            String hashedPassword = passwordService.hashPassword("1234");
            mahasiswaRepository.insert(new Mahasiswa("220001", "Budi Santoso", "budi", hashedPassword));
            mahasiswaRepository.insert(new Mahasiswa("220002", "Siti Aminah", "siti", hashedPassword));
        }
        if (nilaiRepository.count() == 0) {
            addNilai("220001", new Nilai("PBO", 80, 85, 88));
            addNilai("220001", new Nilai("Struktur Data", 78, 80, 82));
            addNilai("220002", new Nilai("PBO", 90, 92, 94));
        }
    }

    public Collection<Mahasiswa> getMahasiswa() {
        return mahasiswaRepository.findAll();
    }

    public Optional<Mahasiswa> findMahasiswa(String nim) {
        return mahasiswaRepository.findByNim(nim);
    }

    public Mahasiswa addMahasiswa(String nim, String nama) {
        String hashedPassword = passwordService.hashPassword("1234");
        Mahasiswa m = new Mahasiswa(nim, nama, nim, hashedPassword);
        mahasiswaRepository.insert(m);
        return m;
    }

    public List<Nilai> getNilai(String nim) {
        return nilaiRepository.findByNim(nim);
    }

    public Map<String, List<Nilai>> getNilaiGroupedByMahasiswa() {
        return mahasiswaRepository.findAll().stream()
                .collect(Collectors.toMap(Mahasiswa::getNim, m -> nilaiRepository.findByNim(m.getNim())));
    }

    public long countNilai() {
        return nilaiRepository.count();
    }

    public Nilai addNilai(String nim, Nilai nilai) {
        return nilaiRepository.insert(nim, nilai);
    }

    public Optional<Nilai> findNilaiById(long id) {
        return nilaiRepository.findById(id);
    }

    public boolean updateNilai(long id, Nilai nilai) {
        return nilaiRepository.update(id, nilai);
    }

    public boolean removeNilai(long id) {
        return nilaiRepository.deleteById(id);
    }

    public File generateReportForMahasiswa(String nim) {
        Mahasiswa m = mahasiswaRepository.findByNim(nim).orElse(null);
        List<Nilai> nilai = nilaiRepository.findByNim(nim);
        Map<String, String> data = new HashMap<>();
        data.put("Mahasiswa", m != null ? m.getNama() + " (" + m.getNim() + ")" : nim);
        data.put("Jumlah Mata Kuliah", String.valueOf(nilai.size()));
        double rata = nilai.stream().mapToDouble(Nilai::hitungRataRata).average().orElse(0);
        data.put("Rata-rata nilai", String.format("%.2f", rata));
        return reportService.generateReport(data);
    }

    public boolean changePassword(String nim, String oldPassword, String newPassword) {
        return mahasiswaRepository.findByNim(nim)
                .filter(m -> passwordService.verifyPassword(oldPassword, m.getPassword()))
                .map(m -> {
                    String hashedNewPassword = passwordService.hashPassword(newPassword);
                    return mahasiswaRepository.updatePassword(nim, hashedNewPassword);
                })
                .orElse(false);
    }

    public boolean updateMahasiswa(String nim, String nama, String username) {
        return mahasiswaRepository.update(nim, nama, username);
    }
}

