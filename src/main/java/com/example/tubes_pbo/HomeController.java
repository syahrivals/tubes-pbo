package com.example.tubes_pbo;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tubes_pbo.model.ActivityLog;
import com.example.tubes_pbo.model.Enrollment;
import com.example.tubes_pbo.model.Nilai;
import com.example.tubes_pbo.repository.EnrollmentRepository;
import com.example.tubes_pbo.repository.MataKuliahRepository;
import com.example.tubes_pbo.repository.NotifikasiRepository;
import com.example.tubes_pbo.repository.SemesterRepository;
import com.example.tubes_pbo.service.GradebookService;
import com.example.tubes_pbo.service.LogService;
import com.example.tubes_pbo.service.NotifikasiService;
import com.example.tubes_pbo.service.ValidationService;

@Controller
public class HomeController {

    private final GradebookService gradebookService;
    private final MataKuliahRepository mataKuliahRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SemesterRepository semesterRepository;
    private final ValidationService validationService;
    private final LogService logService;
    private final NotifikasiService notifikasiService;
    private final NotifikasiRepository notifikasiRepository;
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final JdbcTemplate jdbcTemplate;

    public HomeController(GradebookService gradebookService, 
                         MataKuliahRepository mataKuliahRepository,
                         EnrollmentRepository enrollmentRepository,
                         SemesterRepository semesterRepository,
                         ValidationService validationService,
                         LogService logService,
                         NotifikasiService notifikasiService,
                         NotifikasiRepository notifikasiRepository,
                         JdbcTemplate jdbcTemplate) {
        this.gradebookService = gradebookService;
        this.mataKuliahRepository = mataKuliahRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.semesterRepository = semesterRepository;
        this.validationService = validationService;
        this.logService = logService;
        this.notifikasiService = notifikasiService;
        this.notifikasiRepository = notifikasiRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/fix-db")
    @ResponseBody
    public String fixDb() {
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS semester (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "kode VARCHAR(20) UNIQUE NOT NULL, " +
                    "nama VARCHAR(255) NOT NULL, " +
                    "aktif BOOLEAN NOT NULL DEFAULT false)");
            
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS dosen (" +
                    "kode_dosen VARCHAR(32) PRIMARY KEY, " +
                    "nama VARCHAR(255) NOT NULL, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL)");
            
            jdbcTemplate.execute("INSERT IGNORE INTO dosen (kode_dosen, nama, username, password) VALUES " +
                    "('D001', 'Admin Dosen', 'dosen', '1234')");
            
            jdbcTemplate.execute("INSERT IGNORE INTO mata_kuliah (kode, nama, sks, kode_dosen) VALUES " +
                    "('PBO', 'PBO', 3, 'D001'), " +
                    "('SDA', 'Struktur Data', 3, 'D001'), " +
                    "('DBMS', 'Database Management System', 3, 'D001')");
            
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS notifikasi (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "nim VARCHAR(32), " +
                    "kode_dosen VARCHAR(32), " +
                    "pesan TEXT NOT NULL, " +
                    "tipe VARCHAR(20) NOT NULL DEFAULT 'INFO', " +
                    "dibaca TINYINT(1) NOT NULL DEFAULT 0, " +
                    "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_notifikasi_mahasiswa FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_notifikasi_dosen FOREIGN KEY (kode_dosen) REFERENCES dosen(kode_dosen) ON DELETE CASCADE)");
            
            // Add kode_dosen column if it doesn't exist
            try {
                jdbcTemplate.execute("ALTER TABLE notifikasi ADD COLUMN kode_dosen VARCHAR(32)");
                jdbcTemplate.execute("ALTER TABLE notifikasi ADD CONSTRAINT fk_notifikasi_dosen FOREIGN KEY (kode_dosen) REFERENCES dosen(kode_dosen) ON DELETE CASCADE");
            } catch (Exception e) {
                // Column might already exist, ignore
            }
            
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS enrollment (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "nim VARCHAR(32) NOT NULL, " +
                    "mata_kuliah_id BIGINT NOT NULL, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED', " +
                    "enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "semester_id BIGINT, " +
                    "CONSTRAINT fk_enrollment_mahasiswa FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_enrollment_mata_kuliah FOREIGN KEY (mata_kuliah_id) REFERENCES mata_kuliah(id) ON DELETE CASCADE)");
            
            jdbcTemplate.execute("INSERT IGNORE INTO enrollment (nim, mata_kuliah_id, status, enrolled_at) " +
                    "SELECT '220001', mk.id, 'COMPLETED', NOW() FROM mata_kuliah mk WHERE mk.nama IN ('PBO', 'Struktur Data')");
            
            jdbcTemplate.execute("INSERT IGNORE INTO enrollment (nim, mata_kuliah_id, status, enrolled_at) " +
                    "SELECT '220002', mk.id, 'COMPLETED', NOW() FROM mata_kuliah mk WHERE mk.nama = 'PBO'");
            
            jdbcTemplate.execute("INSERT IGNORE INTO semester (kode, nama, aktif) VALUES " +
                    "('2024-1', 'Semester Ganjil 2024/2025', true), " +
                    "('2024-2', 'Semester Genap 2024/2025', false), " +
                    "('2023-1', 'Semester Ganjil 2023/2024', false)");
            
            return "Database fixed: semester and activity_log tables created and seeded.";
        } catch (Exception e) {
            log.error("Error fixing database", e);
            return "Error fixing DB: " + e.getMessage();
        }
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        
        var mahasiswaList = gradebookService.getMahasiswa();
        var nilaiMap = gradebookService.getNilaiGroupedByMahasiswa();
        
        // Calculate grade distribution
        Map<String, Long> gradeDistribution = nilaiMap.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.groupingBy(Nilai::getGrade, Collectors.counting()));
        
        model.addAttribute("mahasiswaList", mahasiswaList);
        model.addAttribute("nilaiMap", nilaiMap);
        model.addAttribute("totalMahasiswa", mahasiswaList.size());
        model.addAttribute("totalNilai", gradebookService.countNilai());
        model.addAttribute("totalMataKuliah", mataKuliahRepository.count());
        model.addAttribute("gradeDistribution", gradeDistribution);
        
        // Get unread notifications count for dosen
        String kodeDosen = (String) session.getAttribute("refId");
        long unreadNotifications = notifikasiRepository.countUnreadByKodeDosen(kodeDosen);
        model.addAttribute("unreadNotifications", unreadNotifications);
        
        model.addAttribute("active", "dashboard");
        return "index";
    }

    @GetMapping("/mahasiswa")
    public String mahasiswa(Model model, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        var mahasiswaList = gradebookService.getMahasiswa();
        System.out.println("=== DEBUG MAHASISWA ===");
        System.out.println("Total mahasiswa dari service: " + mahasiswaList.size());
        mahasiswaList.forEach(m -> System.out.println("NIM: " + m.getNim() + ", Nama: " + m.getNama() + ", Username: " + m.getUsername()));
        model.addAttribute("mahasiswaList", mahasiswaList);
        model.addAttribute("nilaiMap", gradebookService.getNilaiGroupedByMahasiswa());
        model.addAttribute("active", "mahasiswa");
        return "mahasiswa";
    }

    @GetMapping("/nilai")
    public String nilai(Model model, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        model.addAttribute("mahasiswaList", gradebookService.getMahasiswa());
        model.addAttribute("nilaiMap", gradebookService.getNilaiGroupedByMahasiswa());
        model.addAttribute("active", "nilai");
        return "nilai";
    }

    // POST /mahasiswa removed - mahasiswa register sendiri via /register

    @PostMapping("/nilai")
    public String addNilai(@RequestParam String nim,
                           @RequestParam String mataKuliah,
                           @RequestParam double tugas,
                           @RequestParam double uts,
                           @RequestParam double uas,
                           HttpSession session,
                           org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) return "redirect:/login";
        
        // Validasi nilai
        var validation = validationService.validateNilai(tugas, uts, uas);
        if (!validation.isValid()) {
            redirectAttributes.addFlashAttribute("error", validation.errorMessage());
            return "redirect:/nilai";
        }
        
        try {
            gradebookService.addNilai(nim, new Nilai(mataKuliah, tugas, uts, uas));
            String username = (String) session.getAttribute("username");
            logService.logNilaiCreated(username != null ? username : "system", nim, mataKuliah);
            notifikasiService.notifyNilaiBaru(nim, mataKuliah);
            redirectAttributes.addFlashAttribute("success", "Nilai berhasil ditambahkan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambah nilai: " + e.getMessage());
        }
        return "redirect:/nilai";
    }

    @GetMapping("/nilai/{id}/edit")
    public String editNilaiPage(@PathVariable long id, Model model, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        
        gradebookService.findNilaiById(id).ifPresent(nilai -> {
            model.addAttribute("nilai", nilai);
            model.addAttribute("mahasiswaList", gradebookService.getMahasiswa());
            model.addAttribute("mataKuliahList", mataKuliahRepository.findAll());
            model.addAttribute("semesterList", semesterRepository.findAll());
        });
        
        model.addAttribute("active", "nilai");
        return "nilai-edit";
    }

    @PostMapping("/nilai/{id}/edit")
    public String updateNilai(@PathVariable long id,
                              @RequestParam String nim,
                              @RequestParam String mataKuliah,
                              @RequestParam double tugas,
                              @RequestParam double uts,
                              @RequestParam double uas,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) return "redirect:/login";
        
        // Validasi nilai dengan ValidationService
        var validation = validationService.validateNilai(tugas, uts, uas);
        if (!validation.isValid()) {
            redirectAttributes.addFlashAttribute("error", validation.errorMessage());
            return "redirect:/nilai/" + id + "/edit";
        }
        
        try {
            Nilai nilai = new Nilai(mataKuliah, tugas, uts, uas);
            nilai.setId(id);
            gradebookService.updateNilai(id, nilai);
            String username = (String) session.getAttribute("username");
            logService.logNilaiUpdated(username != null ? username : "system", nim, mataKuliah);
            notifikasiService.notifyNilaiDiupdate(nim, mataKuliah);
            redirectAttributes.addFlashAttribute("success", "Nilai berhasil diupdate!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal update nilai: " + e.getMessage());
        }
        
        return "redirect:/nilai";
    }

    @PostMapping("/nilai/{id}/delete")
    public String deleteNilai(@PathVariable long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) return "redirect:/login";
        try {
            var nilaiOpt = gradebookService.findNilaiById(id);
            if (nilaiOpt.isPresent()) {
                Nilai nilai = nilaiOpt.get();
                gradebookService.removeNilai(id);
                String username = (String) session.getAttribute("username");
                logService.logNilaiDeleted(username != null ? username : "system", 
                    nilai.getNim() != null ? nilai.getNim() : "unknown", 
                    nilai.getMataKuliah());
            }
            redirectAttributes.addFlashAttribute("success", "Nilai berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus nilai: " + e.getMessage());
        }
        return "redirect:/nilai";
    }

    @GetMapping("/logs")
    public String logsPage(Model model, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        try {
            List<ActivityLog> logs = logService.findAll(100);
            model.addAttribute("logs", logs);
        } catch (Exception e) {
            model.addAttribute("logs", java.util.Collections.emptyList());
            model.addAttribute("error", "Gagal memuat logs: " + e.getMessage());
        }
        model.addAttribute("active", "logs");
        return "logs";
    }

    @GetMapping("/api/enrollments/{nim}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEnrollmentsByNim(@PathVariable String nim, HttpSession session) {
        if (!isDosen(session)) {
            return ResponseEntity.status(401).build();
        }
        
        List<Enrollment> enrollments = enrollmentRepository.findByNim(nim);
        List<Map<String, Object>> courses = enrollments.stream()
            .filter(e -> "ENROLLED".equals(e.getStatus()) || "COMPLETED".equals(e.getStatus()))
            .map(e -> {
                Map<String, Object> course = new HashMap<>();
                course.put("id", e.getMataKuliahId());
                course.put("kode", e.getKodeMataKuliah());
                course.put("nama", e.getNamaMataKuliah());
                return course;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("courses", courses);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/{nim}")
    public ResponseEntity<FileSystemResource> downloadReport(@PathVariable String nim, HttpSession session) {
        if (!isDosen(session)) return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/login").build();
        File file = gradebookService.generateReportForMahasiswa(nim);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-" + nim + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(new FileSystemResource(file));
    }

    @GetMapping("/enrollments")
    public String pendingEnrollments(Model model, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        try {
            List<Enrollment> pendingEnrollments = enrollmentRepository.findAll()
                .stream()
                .filter(e -> "PENDING".equals(e.getStatus()))
                .collect(Collectors.toList());
            
            model.addAttribute("pendingEnrollments", pendingEnrollments);
            model.addAttribute("active", "enrollments");
            return "enrollments";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal memuat pending enrollments: " + e.getMessage());
            model.addAttribute("pendingEnrollments", List.of());
            model.addAttribute("active", "enrollments");
            return "enrollments";
        }
    }

    @PostMapping("/enrollments/{id}/approve")
    public String approveEnrollment(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) return "redirect:/login";
        try {
            enrollmentRepository.findById(id).ifPresent(enrollment -> {
                enrollment.setStatus("ENROLLED");
                enrollmentRepository.update(enrollment);
                
                // Notify student
                mataKuliahRepository.findById(enrollment.getMataKuliahId()).ifPresent(mk -> {
                    notifikasiService.notifyEnrollmentApproved(enrollment.getNim(), mk.getNama());
                });
                
                // Log activity
                String username = (String) session.getAttribute("username");
                logService.logActivity("DOSEN", username, "APPROVE", "ENROLLMENT", 
                    "Menyetujui pendaftaran NIM " + enrollment.getNim() + " untuk mata kuliah ID " + enrollment.getMataKuliahId());
            });
            redirectAttributes.addFlashAttribute("success", "Enrollment berhasil disetujui!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menyetujui enrollment: " + e.getMessage());
        }
        return "redirect:/enrollments";
    }

    @PostMapping("/enrollments/{id}/reject")
    public String rejectEnrollment(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) return "redirect:/login";
        try {
            enrollmentRepository.findById(id).ifPresent(enrollment -> {
                enrollmentRepository.delete(id);
                
                // Notify student
                mataKuliahRepository.findById(enrollment.getMataKuliahId()).ifPresent(mk -> {
                    notifikasiService.notifyEnrollmentRejected(enrollment.getNim(), mk.getNama());
                });
                
                // Log activity
                String username = (String) session.getAttribute("username");
                logService.logActivity("DOSEN", username, "REJECT", "ENROLLMENT", 
                    "Menolak pendaftaran NIM " + enrollment.getNim() + " untuk mata kuliah ID " + enrollment.getMataKuliahId());
            });
            redirectAttributes.addFlashAttribute("success", "Enrollment berhasil ditolak!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menolak enrollment: " + e.getMessage());
        }
        return "redirect:/enrollments";
    }

    @GetMapping("/notifications")
    public String notifications(Model model, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        try {
            String kodeDosen = (String) session.getAttribute("refId");
            model.addAttribute("notifications", notifikasiRepository.findByKodeDosen(kodeDosen));
            model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByKodeDosen(kodeDosen));
            model.addAttribute("active", "notifications");
            return "notifications";
        } catch (Exception e) {
            model.addAttribute("notifications", java.util.Collections.emptyList());
            model.addAttribute("unreadNotifications", 0);
            model.addAttribute("active", "notifications");
            return "notifications";
        }
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationAsRead(@PathVariable Long id, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        notifikasiRepository.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsAsRead(HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        String kodeDosen = (String) session.getAttribute("refId");
        notifikasiRepository.markAllAsReadByKodeDosen(kodeDosen);
        return "redirect:/notifications";
    }

    private boolean isDosen(HttpSession session) {
        Object role = session.getAttribute("userRole");
        return role != null && "DOSEN".equals(role.toString());
    }
}