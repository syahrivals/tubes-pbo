package com.example.tubes_pbo;

import com.example.tubes_pbo.service.AuthService;
import com.example.tubes_pbo.service.LogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthService authService;
    private final LogService logService;

    public AuthController(AuthService authService, LogService logService) {
        this.authService = authService;
        this.logService = logService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("active", "login");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam(required = false) String username,
                          @RequestParam(required = false) String password,
                          Model model,
                          HttpSession session) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username tidak boleh kosong!");
            model.addAttribute("active", "login");
            return "login";
        }
        
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Password tidak boleh kosong!");
            model.addAttribute("active", "login");
            model.addAttribute("username", username);
            return "login";
        }

        try {
            return authService.login(username.trim(), password)
                    .map(user -> {
                        session.setAttribute("userRole", user.role());
                        session.setAttribute("username", user.username());
                        session.setAttribute("displayName", user.displayName());
                        session.setAttribute("refId", user.refId());
                        
                        // Log login activity
                        try {
                            logService.logLogin(user.role(), user.username());
                        } catch (Exception e) {
                            System.err.println("Failed to log login activity: " + e.getMessage());
                            e.printStackTrace();
                        }
                        
                        if ("DOSEN".equals(user.role())) {
                            return "redirect:/";
                        }
                        return "redirect:/student";
                    })
                    .orElseGet(() -> {
                        model.addAttribute("error", "Username atau password salah! Silakan coba lagi.");
                        model.addAttribute("active", "login");
                        model.addAttribute("username", username);
                        return "login";
                    });
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Terjadi kesalahan sistem. Silakan coba lagi.");
            model.addAttribute("active", "login");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        String username = (String) session.getAttribute("username");
        
        // Log logout activity
        if (userRole != null && username != null) {
            try {
                logService.logLogout(userRole, username);
            } catch (Exception e) {
                System.err.println("Failed to log logout activity: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        session.invalidate();
        return "redirect:/login";
    }
}

