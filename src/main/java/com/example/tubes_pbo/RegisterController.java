package com.example.tubes_pbo;

import com.example.tubes_pbo.model.Mahasiswa;
import com.example.tubes_pbo.repository.MahasiswaRepository;
import com.example.tubes_pbo.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    private final MahasiswaRepository mahasiswaRepository;
    private final PasswordService passwordService;

    public RegisterController(MahasiswaRepository mahasiswaRepository, PasswordService passwordService) {
        this.mahasiswaRepository = mahasiswaRepository;
        this.passwordService = passwordService;
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        // Redirect if already logged in
        if (session.getAttribute("userRole") != null) {
            return "redirect:/";
        }
        model.addAttribute("active", "register");
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam(required = false) String nim,
                             @RequestParam(required = false) String nama,
                             @RequestParam(required = false) String username,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) String confirmPassword,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        // Validation
        if (nim == null || nim.trim().isEmpty()) {
            model.addAttribute("error", "NIM tidak boleh kosong!");
            model.addAttribute("active", "register");
            return "register";
        }

        if (nama == null || nama.trim().isEmpty()) {
            model.addAttribute("error", "Nama tidak boleh kosong!");
            model.addAttribute("nim", nim);
            model.addAttribute("active", "register");
            return "register";
        }

        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username tidak boleh kosong!");
            model.addAttribute("nim", nim);
            model.addAttribute("nama", nama);
            model.addAttribute("active", "register");
            return "register";
        }

        if (password == null || password.length() < 4) {
            model.addAttribute("error", "Password minimal 4 karakter!");
            model.addAttribute("nim", nim);
            model.addAttribute("nama", nama);
            model.addAttribute("username", username);
            model.addAttribute("active", "register");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Password dan konfirmasi password tidak cocok!");
            model.addAttribute("nim", nim);
            model.addAttribute("nama", nama);
            model.addAttribute("username", username);
            model.addAttribute("active", "register");
            return "register";
        }

        // Check if NIM already exists
        if (mahasiswaRepository.findByNim(nim.trim()).isPresent()) {
            model.addAttribute("error", "NIM sudah terdaftar!");
            model.addAttribute("nama", nama);
            model.addAttribute("username", username);
            model.addAttribute("active", "register");
            return "register";
        }

        // Check if username already exists
        if (mahasiswaRepository.findByUsername(username.trim()).isPresent()) {
            model.addAttribute("error", "Username sudah digunakan!");
            model.addAttribute("nim", nim);
            model.addAttribute("nama", nama);
            model.addAttribute("active", "register");
            return "register";
        }

        try {
            // Hash password
            String hashedPassword = passwordService.hashPassword(password);
            
            // Create mahasiswa
            Mahasiswa mahasiswa = new Mahasiswa(nim.trim(), nama.trim(), username.trim(), hashedPassword);
            mahasiswaRepository.insert(mahasiswa);
            
            redirectAttributes.addFlashAttribute("success", 
                "Registrasi berhasil! Silakan login dengan username dan password Anda.");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan sistem: " + e.getMessage());
            model.addAttribute("nim", nim);
            model.addAttribute("nama", nama);
            model.addAttribute("username", username);
            model.addAttribute("active", "register");
            return "register";
        }
    }
}

