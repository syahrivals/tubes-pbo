package com.example.tubes_pbo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tubes_pbo.model.Enrollment;
import com.example.tubes_pbo.model.Mahasiswa;
import com.example.tubes_pbo.model.MataKuliah;
import com.example.tubes_pbo.model.Nilai;
import com.example.tubes_pbo.repository.EnrollmentRepository;
import com.example.tubes_pbo.repository.MataKuliahRepository;
import com.example.tubes_pbo.repository.NotifikasiRepository;
import com.example.tubes_pbo.service.GradebookService;
import com.example.tubes_pbo.service.NotifikasiService;
import com.example.tubes_pbo.service.PdfExportService;

@Controller
public class StudentController {

    private final GradebookService gradebookService;
    private final PdfExportService pdfExportService;
    private final MataKuliahRepository mataKuliahRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotifikasiRepository notifikasiRepository;
    private final NotifikasiService notifikasiService;

    public StudentController(GradebookService gradebookService, 
                            PdfExportService pdfExportService,
                            MataKuliahRepository mataKuliahRepository,
                            EnrollmentRepository enrollmentRepository,
                            NotifikasiRepository notifikasiRepository,
                            NotifikasiService notifikasiService) {
        this.gradebookService = gradebookService;
        this.pdfExportService = pdfExportService;
        this.mataKuliahRepository = mataKuliahRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.notifikasiRepository = notifikasiRepository;
        this.notifikasiService = notifikasiService;
    }

    @GetMapping("/student")
    public String studentDashboard(Model model, HttpSession session) {
        if (!isMahasiswa(session)) return "redirect:/login";
        try {
            String nim = (String) session.getAttribute("refId");
            List<Nilai> nilai = gradebookService.getNilai(nim);
            double rata = nilai.stream().mapToDouble(Nilai::hitungRataRata).average().orElse(0);
            
            // Calculate statistics
            Map<String, Long> gradeDistribution = nilai.stream()
                .collect(Collectors.groupingBy(Nilai::getGrade, Collectors.counting()));
            
            double highest = nilai.stream().mapToDouble(Nilai::hitungRataRata).max().orElse(0);
            double lowest = nilai.stream().mapToDouble(Nilai::hitungRataRata).min().orElse(0);
            
            // Get unread notifications count
            long unreadCount = notifikasiRepository.countUnreadByNim(nim);
            
            model.addAttribute("nama", session.getAttribute("displayName"));
            model.addAttribute("nim", nim);
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("nilaiList", nilai);
            model.addAttribute("rata", rata);
            model.addAttribute("totalMataKuliah", nilai.size());
            model.addAttribute("gradeDistribution", gradeDistribution);
            model.addAttribute("highest", highest);
            model.addAttribute("lowest", lowest);
            model.addAttribute("unreadNotifications", unreadCount);
            model.addAttribute("active", "student");
            return "student";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal memuat dashboard: " + e.getMessage());
            model.addAttribute("nama", session.getAttribute("displayName"));
            model.addAttribute("nim", session.getAttribute("refId"));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("nilaiList", java.util.Collections.emptyList());
            model.addAttribute("rata", 0.0);
            model.addAttribute("totalMataKuliah", 0);
            model.addAttribute("gradeDistribution", java.util.Collections.emptyMap());
            model.addAttribute("highest", 0.0);
            model.addAttribute("lowest", 0.0);
            model.addAttribute("unreadNotifications", 0);
            model.addAttribute("active", "student");
            return "student";
        }
    }

    @GetMapping("/student/notifications")
    public String notificationsPage(Model model, HttpSession session) {
        if (!isMahasiswa(session)) return "redirect:/login";
        try {
            String nim = (String) session.getAttribute("refId");
            model.addAttribute("notifications", notifikasiRepository.findByNim(nim));
            model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByNim(nim));
            model.addAttribute("active", "notifications");
            return "student-notifications";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal memuat notifikasi: " + e.getMessage());
            model.addAttribute("notifications", java.util.Collections.emptyList());
            model.addAttribute("unreadNotifications", 0);
            model.addAttribute("active", "notifications");
            return "student-notifications";
        }
    }

    @PostMapping("/student/notifications/{id}/read")
    public String markNotificationAsRead(@PathVariable Long id, HttpSession session) {
        if (!isMahasiswa(session)) return "redirect:/login";
        notifikasiRepository.markAsRead(id);
        return "redirect:/student/notifications";
    }

    @PostMapping("/student/notifications/read-all")
    public String markAllNotificationsAsRead(HttpSession session) {
        if (!isMahasiswa(session)) return "redirect:/login";
        String nim = (String) session.getAttribute("refId");
        notifikasiRepository.markAllAsRead(nim);
        return "redirect:/student/notifications";
    }

    @GetMapping("/student/profile")
    public String studentProfile(Model model, HttpSession session) {
        if (!isMahasiswa(session)) return "redirect:/login";
        try {
            String nim = (String) session.getAttribute("refId");
            Mahasiswa mahasiswa = gradebookService.findMahasiswa(nim).orElse(null);
            
            model.addAttribute("mahasiswa", mahasiswa);
            model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByNim(nim));
            model.addAttribute("active", "profile");
            return "student-profile";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal memuat profile: " + e.getMessage());
            model.addAttribute("mahasiswa", null);
            model.addAttribute("unreadNotifications", 0);
            model.addAttribute("active", "profile");
            return "student-profile";
        }
    }

    @PostMapping("/student/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isMahasiswa(session)) return "redirect:/login";
        
        String nim = (String) session.getAttribute("refId");
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Password baru tidak cocok!");
            return "redirect:/student/profile";
        }
        
        if (newPassword.length() < 4) {
            redirectAttributes.addFlashAttribute("error", "Password minimal 4 karakter!");
            return "redirect:/student/profile";
        }
        
        boolean success = gradebookService.changePassword(nim, oldPassword, newPassword);
        
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Password berhasil diubah!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Password lama salah!");
        }
        
        return "redirect:/student/profile";
    }

    @PostMapping("/student/profile/update")
    public String updateProfile(@RequestParam String nama,
                                @RequestParam String username,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!isMahasiswa(session)) return "redirect:/login";
        
        String nim = (String) session.getAttribute("refId");
        
        // Validasi
        if (nama == null || nama.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nama tidak boleh kosong!");
            return "redirect:/student/profile";
        }
        
        try {
            boolean success = gradebookService.updateMahasiswa(nim, nama.trim(), username.trim());
            if (success) {
                session.setAttribute("displayName", nama.trim());
                redirectAttributes.addFlashAttribute("success", "Profile berhasil diupdate!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Gagal update profile!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal update: " + e.getMessage());
        }
        
        return "redirect:/student/profile";
    }

    @GetMapping("/student/export-pdf")
    public ResponseEntity<byte[]> exportPdf(HttpSession session) {
        if (!isMahasiswa(session)) {
            return ResponseEntity.status(302)
                    .header(HttpHeaders.LOCATION, "/login")
                    .build();
        }

        String nim = (String) session.getAttribute("refId");
        Mahasiswa mahasiswa = gradebookService.findMahasiswa(nim).orElse(null);
        
        if (mahasiswa == null) {
            return ResponseEntity.notFound().build();
        }

        List<Nilai> nilaiList = gradebookService.getNilai(nim);
        byte[] pdfBytes = pdfExportService.generateTranscriptPdf(mahasiswa, nilaiList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "transkrip-" + nim + ".pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/student/courses")
    public String browseCourses(Model model, HttpSession session) {
        if (!isMahasiswa(session)) return "redirect:/login";
        try {
            String nim = (String) session.getAttribute("refId");
            List<MataKuliah> allCourses = mataKuliahRepository.findAll();
            List<Enrollment> myEnrollments = enrollmentRepository.findByNim(nim);
            
            // Mark which courses are already enrolled
            List<Long> enrolledIds = myEnrollments.stream()
                .map(Enrollment::getMataKuliahId)
                .collect(Collectors.toList());
            
            model.addAttribute("courses", allCourses);
            model.addAttribute("enrolledIds", enrolledIds);
            model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByNim(nim));
            model.addAttribute("active", "browse-courses");
            return "student-browse-courses";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal memuat courses: " + e.getMessage());
            model.addAttribute("courses", java.util.Collections.emptyList());
            model.addAttribute("enrolledIds", java.util.Collections.emptyList());
            model.addAttribute("unreadNotifications", 0);
            model.addAttribute("active", "browse-courses");
            return "student-browse-courses";
        }
    }

    @PostMapping("/student/enroll")
    public String enrollCourse(@RequestParam Long courseId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isMahasiswa(session)) return "redirect:/login";
        
        String nim = (String) session.getAttribute("refId");
        
        // Check if already enrolled
        if (enrollmentRepository.findByNimAndMataKuliahId(nim, courseId).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Anda sudah terdaftar di mata kuliah ini!");
            return "redirect:/student/courses";
        }
        
        try {
            Enrollment enrollment = new Enrollment(nim, courseId, "PENDING");
            enrollmentRepository.insert(enrollment);
            mataKuliahRepository.findById(courseId).ifPresent(mk -> {
                notifikasiService.notifyEnrollmentRequest(nim, mk.getNama());
                // Notify dosen about new enrollment request
                notifikasiService.notifyNewEnrollmentRequest(
                    mk.getKodeDosen(), 
                    (String) session.getAttribute("displayName"), 
                    nim, 
                    mk.getNama()
                );
            });
            redirectAttributes.addFlashAttribute("success", "Permintaan pendaftaran mata kuliah telah dikirim ke dosen!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengirim permintaan: " + e.getMessage());
        }
        
        return "redirect:/student/enrollments";
    }

    @GetMapping("/student/enrollments")
    public String myEnrollments(Model model, HttpSession session) {
        if (!isMahasiswa(session)) return "redirect:/login";
        try {
            String nim = (String) session.getAttribute("refId");
            List<Enrollment> enrollments = enrollmentRepository.findByNim(nim)
                .stream()
                .filter(e -> "ENROLLED".equals(e.getStatus()) || "COMPLETED".equals(e.getStatus()))
                .collect(java.util.stream.Collectors.toList());
            
            int totalSks = enrollments.stream()
                .filter(e -> "ENROLLED".equals(e.getStatus()))
                .mapToInt(Enrollment::getSks)
                .sum();
            
            model.addAttribute("enrollments", enrollments);
            model.addAttribute("totalSks", totalSks);
            model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByNim(nim));
            model.addAttribute("active", "enrollments");
            return "student-enrollments";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal memuat enrollments: " + e.getMessage());
            model.addAttribute("enrollments", java.util.Collections.emptyList());
            model.addAttribute("totalSks", 0);
            model.addAttribute("unreadNotifications", 0);
            model.addAttribute("active", "enrollments");
            return "student-enrollments";
        }
    }

    @PostMapping("/student/enrollments/{id}/drop")
    public String dropCourse(@PathVariable Long id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!isMahasiswa(session)) return "redirect:/login";
        
        try {
            enrollmentRepository.updateStatus(id, "DROPPED");
            redirectAttributes.addFlashAttribute("success", "Mata kuliah berhasil di-drop!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal drop mata kuliah: " + e.getMessage());
        }
        
        return "redirect:/student/enrollments";
    }

    private boolean isMahasiswa(HttpSession session) {
        Object role = session.getAttribute("userRole");
        return role != null && "MAHASISWA".equals(role.toString());
    }
}

