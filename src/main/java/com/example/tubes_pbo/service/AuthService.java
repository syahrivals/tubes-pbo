package com.example.tubes_pbo.service;

import com.example.tubes_pbo.repository.DosenRepository;
import com.example.tubes_pbo.repository.MahasiswaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final DosenRepository dosenRepository;
    private final MahasiswaRepository mahasiswaRepository;
    private final PasswordService passwordService;

    public AuthService(DosenRepository dosenRepository, MahasiswaRepository mahasiswaRepository, PasswordService passwordService) {
        this.dosenRepository = dosenRepository;
        this.mahasiswaRepository = mahasiswaRepository;
        this.passwordService = passwordService;
    }

    public Optional<AuthUser> login(String username, String password) {
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Username: " + username);
        System.out.println("Password length: " + password.length());
        
        Optional<AuthUser> dosen = dosenRepository.findByUsername(username)
                .map(d -> {
                    System.out.println("Found Dosen: " + d.getUsername());
                    System.out.println("DB Password: " + d.getPassword());
                    
                    boolean isValid = false;
                    
                    // Check if password is BCrypt hash (starts with $2a$ or $2b$)
                    if (d.getPassword().startsWith("$2a$") || d.getPassword().startsWith("$2b$")) {
                        // Use BCrypt verification
                        try {
                            isValid = passwordService.verifyPassword(password, d.getPassword());
                            System.out.println("BCrypt verify result: " + isValid);
                        } catch (Exception e) {
                            System.out.println("BCrypt failed: " + e.getMessage());
                        }
                    } else {
                        // Plain text comparison for backward compatibility
                        isValid = d.getPassword().equals(password);
                        System.out.println("Plain text verify result: " + isValid);
                    }
                    
                    if (isValid) {
                        return new AuthUser("DOSEN", d.getUsername(), d.getNama(), d.getKodeDosen());
                    }
                    return null;
                })
                .filter(user -> user != null);
                
        if (dosen.isPresent()) {
            System.out.println("Login successful as DOSEN");
            return dosen;
        }
        
        System.out.println("Trying Mahasiswa...");
        Optional<AuthUser> mahasiswa = mahasiswaRepository.findByUsername(username)
                .map(m -> {
                    System.out.println("Found Mahasiswa: " + m.getUsername());
                    System.out.println("DB Password: " + m.getPassword());
                    
                    boolean isValid = false;
                    
                    // Check if password is BCrypt hash (starts with $2a$ or $2b$)
                    if (m.getPassword().startsWith("$2a$") || m.getPassword().startsWith("$2b$")) {
                        // Use BCrypt verification
                        try {
                            isValid = passwordService.verifyPassword(password, m.getPassword());
                            System.out.println("BCrypt verify result: " + isValid);
                        } catch (Exception e) {
                            System.out.println("BCrypt failed: " + e.getMessage());
                        }
                    } else {
                        // Plain text comparison for backward compatibility
                        isValid = m.getPassword().equals(password);
                        System.out.println("Plain text verify result: " + isValid);
                    }
                    
                    if (isValid) {
                        return new AuthUser("MAHASISWA", m.getUsername(), m.getNama(), m.getNim());
                    }
                    return null;
                })
                .filter(user -> user != null);
                
        if (mahasiswa.isPresent()) {
            System.out.println("Login successful as MAHASISWA");
        } else {
            System.out.println("Login FAILED - no matching user found");
        }
        
        return mahasiswa;
    }

    public record AuthUser(String role, String username, String displayName, String refId) {
    }
}

