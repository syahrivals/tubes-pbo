package com.example.tubes_pbo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import jakarta.servlet.http.HttpSession;

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
    }

    @GetMapping("/student/notifications")
    public String notificationsPage(Model model, HttpSession session) {
        if (!isMahasiswa(session)) return "redirect:/login";
        String nim = (String) session.getAttribute("refId");
        model.addAttribute("notifications", notifikasiRepository.findByNim(nim));
        model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByNim(nim));
        model.addAttribute("active", "notifications");
        return "student-notifications";
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
        String nim = (String) session.getAttribute("refId");
        Mahasiswa mahasiswa = gradebookService.findMahasiswa(nim).orElse(null);
        
        // Extract data from mahasiswa object or use session as fallback
        if (mahasiswa != null) {
            model.addAttribute("nama", mahasiswa.getNama());
            model.addAttribute("nim", mahasiswa.getNim());
            model.addAttribute("username", mahasiswa.getUsername());
        } else {
            // Fallback to session data if mahasiswa not found
            model.addAttribute("nama", session.getAttribute("displayName"));
            model.addAttribute("nim", nim);
            model.addAttribute("username", session.getAttribute("username"));
        }
        
        model.addAttribute("mahasiswa", mahasiswa);
        model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByNim(nim));
        model.addAttribute("active", "profile");
        return "student-profile";
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
            redirectAttributes.addFlashAttribute("errorPwd", "Password baru tidak cocok!");
            return "redirect:/student/profile";
        }
        
        if (newPassword.length() < 4) {
            redirectAttributes.addFlashAttribute("errorPwd", "Password minimal 4 karakter!");
            return "redirect:/student/profile";
        }
        
        boolean success = gradebookService.changePassword(nim, oldPassword, newPassword);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successPwd", "Password berhasil diubah!");
        } else {
            redirectAttributes.addFlashAttribute("errorPwd", "Password lama salah!");
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
        
        String nim = (String) session.getAttribute("refId");
        List<MataKuliah> allCourses = mataKuliahRepository.findAll();
        List<Enrollment> myEnrollments = enrollmentRepository.findByNim(nim);
        
        // Mark which courses are already enrolled (hanya yang status aktif, bukan DROPPED)
        List<Long> enrolledIds = myEnrollments.stream()
            .filter(e -> "ENROLLED".equals(e.getStatus()) || "APPROVED".equals(e.getStatus()))
            .map(Enrollment::getMataKuliahId)
            .collect(Collectors.toList());
        
        model.addAttribute("courses", allCourses);
        model.addAttribute("enrolledIds", enrolledIds);
        model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByNim(nim));
        model.addAttribute("active", "browse-courses");
        return "student-browse-courses";
    }

    @PostMapping("/student/enroll")
    public String enrollCourse(@RequestParam Long courseId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isMahasiswa(session)) return "redirect:/login";
        
        String nim = (String) session.getAttribute("refId");
        
        // Check if already enrolled (hanya cek yang aktif, bukan DROPPED)
        var existingEnrollment = enrollmentRepository.findByNimAndMataKuliahId(nim, courseId);
        if (existingEnrollment.isPresent()) {
            String status = existingEnrollment.get().getStatus();
            if ("ENROLLED".equals(status) || "APPROVED".equals(status)) {
                redirectAttributes.addFlashAttribute("error", "Anda sudah terdaftar di mata kuliah ini!");
                return "redirect:/student/courses";
            }
        }
        
        try {
            // Enroll langsung tanpa perlu approval dosen
            Enrollment enrollment = new Enrollment(nim, courseId, "ENROLLED");
            enrollmentRepository.insert(enrollment);
            mataKuliahRepository.findById(courseId).ifPresent(mk -> {
                // Notify mahasiswa bahwa enrollment berhasil
                notifikasiService.notifyEnrollment(nim, mk.getNama());
            });
            redirectAttributes.addFlashAttribute("success", "Berhasil terdaftar di mata kuliah!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mendaftar: " + e.getMessage());
        }
        
        return "redirect:/student/enrollments";
    }

    @PostMapping("/student/courses/{id}/enroll")
    public String enrollCourseById(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isMahasiswa(session)) return "redirect:/login";
        
        String nim = (String) session.getAttribute("refId");
        
        // Check if already enrolled (hanya cek yang aktif, bukan DROPPED)
        var existingEnrollment = enrollmentRepository.findByNimAndMataKuliahId(nim, id);
        if (existingEnrollment.isPresent()) {
            String status = existingEnrollment.get().getStatus();
            if ("ENROLLED".equals(status) || "APPROVED".equals(status)) {
                redirectAttributes.addFlashAttribute("error", "Anda sudah terdaftar di mata kuliah ini!");
                return "redirect:/student/courses";
            }
        }
        
        try {
            // Enroll langsung tanpa perlu approval dosen
            Enrollment enrollment = new Enrollment(nim, id, "ENROLLED");
            enrollmentRepository.insert(enrollment);
            mataKuliahRepository.findById(id).ifPresent(mk -> {
                // Notify mahasiswa bahwa enrollment berhasil
                notifikasiService.notifyEnrollment(nim, mk.getNama());
            });
            redirectAttributes.addFlashAttribute("success", "Berhasil terdaftar di mata kuliah!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mendaftar: " + e.getMessage());
        }
        
        return "redirect:/student/enrollments";
    }

    @GetMapping("/student/enrollments")
    public String myEnrollments(Model model, HttpSession session) {
        if (!isMahasiswa(session)) return "redirect:/login";
        
        String nim = (String) session.getAttribute("refId");
        List<Enrollment> allEnrollments = enrollmentRepository.findByNim(nim);
        
        // Filter hanya enrollment yang aktif (ENROLLED atau APPROVED)
        List<Enrollment> enrollments = allEnrollments.stream()
            .filter(e -> "ENROLLED".equals(e.getStatus()) || "APPROVED".equals(e.getStatus()))
            .collect(Collectors.toList());
        
        int totalSks = enrollments.stream()
            .mapToInt(Enrollment::getSks)
            .sum();
        
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("totalSks", totalSks);
        model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByNim(nim));
        model.addAttribute("active", "enrollments");
        return "student-enrollments";
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

