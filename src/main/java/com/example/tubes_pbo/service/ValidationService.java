package com.example.tubes_pbo.service;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public ValidationResult validateNilai(double tugas, double uts, double uas) {
        StringBuilder errors = new StringBuilder();
        
        if (tugas < 0 || tugas > 100) {
            errors.append("Nilai Tugas harus antara 0-100. ");
        }
        if (uts < 0 || uts > 100) {
            errors.append("Nilai UTS harus antara 0-100. ");
        }
        if (uas < 0 || uas > 100) {
            errors.append("Nilai UAS harus antara 0-100. ");
        }
        
        if (Double.isNaN(tugas) || Double.isInfinite(tugas)) {
            errors.append("Nilai Tugas tidak valid. ");
        }
        if (Double.isNaN(uts) || Double.isInfinite(uts)) {
            errors.append("Nilai UTS tidak valid. ");
        }
        if (Double.isNaN(uas) || Double.isInfinite(uas)) {
            errors.append("Nilai UAS tidak valid. ");
        }
        
        boolean isValid = errors.length() == 0;
        return new ValidationResult(isValid, errors.toString().trim());
    }

    public ValidationResult validateNim(String nim) {
        if (nim == null || nim.trim().isEmpty()) {
            return new ValidationResult(false, "NIM tidak boleh kosong");
        }
        if (!nim.matches("^[0-9]{6,10}$")) {
            return new ValidationResult(false, "NIM harus berupa angka 6-10 digit");
        }
        return new ValidationResult(true, "");
    }

    public ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Username tidak boleh kosong");
        }
        if (username.length() < 3 || username.length() > 20) {
            return new ValidationResult(false, "Username harus 3-20 karakter");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return new ValidationResult(false, "Username hanya boleh huruf, angka, dan underscore");
        }
        return new ValidationResult(true, "");
    }

    public ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Password tidak boleh kosong");
        }
        if (password.length() < 4) {
            return new ValidationResult(false, "Password minimal 4 karakter");
        }
        if (password.length() > 50) {
            return new ValidationResult(false, "Password maksimal 50 karakter");
        }
        return new ValidationResult(true, "");
    }

    public ValidationResult validateMataKuliahKode(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            return new ValidationResult(false, "Kode mata kuliah tidak boleh kosong");
        }
        if (!kode.matches("^[A-Z0-9]{3,10}$")) {
            return new ValidationResult(false, "Kode harus huruf besar dan angka, 3-10 karakter");
        }
        return new ValidationResult(true, "");
    }

    public ValidationResult validateSks(int sks) {
        if (sks < 1 || sks > 6) {
            return new ValidationResult(false, "SKS harus antara 1-6");
        }
        return new ValidationResult(true, "");
    }

    public record ValidationResult(boolean isValid, String errorMessage) {
    }
}

