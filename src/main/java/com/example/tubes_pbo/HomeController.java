package com.example.tubes_pbo;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final GradebookService gradebookService;
    private final MataKuliahRepository mataKuliahRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotifikasiRepository notifikasiRepository;
    private final SemesterRepository semesterRepository;
    private final ValidationService validationService;
    private final LogService logService;
    private final NotifikasiService notifikasiService;
    private final JdbcTemplate jdbcTemplate;

    public HomeController(GradebookService gradebookService,
            MataKuliahRepository mataKuliahRepository,
            EnrollmentRepository enrollmentRepository,
            NotifikasiRepository notifikasiRepository,
            SemesterRepository semesterRepository,
            ValidationService validationService,
            LogService logService,
            NotifikasiService notifikasiService,
            JdbcTemplate jdbcTemplate) {
        this.gradebookService = gradebookService;
        this.mataKuliahRepository = mataKuliahRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.notifikasiRepository = notifikasiRepository;
        this.semesterRepository = semesterRepository;
        this.validationService = validationService;
        this.logService = logService;
        this.notifikasiService = notifikasiService;
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

            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS activity_log (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_role VARCHAR(20) NOT NULL, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "action VARCHAR(20) NOT NULL, " +
                    "entity_type VARCHAR(50), " +
                    "entity_id VARCHAR(255), " +
                    "description TEXT, " +
                    "timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "INDEX idx_user_role (user_role), " +
                    "INDEX idx_entity_type (entity_type), " +
                    "INDEX idx_timestamp (timestamp))");

            jdbcTemplate.execute("INSERT IGNORE INTO semester (kode, nama, aktif) VALUES " +
                    "('2024-1', 'Semester Ganjil 2024/2025', true), " +
                    "('2024-2', 'Semester Genap 2024/2025', false), " +
                    "('2023-1', 'Semester Ganjil 2023/2024', false)");

            return "Database fixed: semester and activity_log tables created and seeded.";
        } catch (Exception e) {
            return "Error fixing DB: " + e.getMessage();
        }
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        if (!isDosen(session))
            return "redirect:/login";

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
        model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByDosen());
        model.addAttribute("active", "dashboard");
        return "index";
    }

    @GetMapping("/notifications")
    public String notificationsPage(Model model, HttpSession session) {
        if (!isDosen(session))
            return "redirect:/login";
        model.addAttribute("notifications", notifikasiRepository.findByDosen());
        model.addAttribute("unreadNotifications", notifikasiRepository.countUnreadByDosen());
        model.addAttribute("active", "notifications");
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationAsRead(@PathVariable Long id, HttpSession session) {
        if (!isDosen(session))
            return "redirect:/login";
        notifikasiRepository.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsAsRead(HttpSession session) {
        if (!isDosen(session))
            return "redirect:/login";
        // Mark all dosen notifications as read (nim is null or empty)
        jdbcTemplate.update("UPDATE notifikasi SET dibaca = true WHERE nim IS NULL OR nim = ''");
        return "redirect:/notifications";
    }

    @GetMapping("/mahasiswa")
    public String mahasiswa(Model model, HttpSession session) {
        if (!isDosen(session))
            return "redirect:/login";
        var mahasiswaList = gradebookService.getMahasiswa();
        System.out.println("=== DEBUG MAHASISWA ===");
        System.out.println("Total mahasiswa dari service: " + mahasiswaList.size());
        mahasiswaList.forEach(m -> System.out
                .println("NIM: " + m.getNim() + ", Nama: " + m.getNama() + ", Username: " + m.getUsername()));
        model.addAttribute("list", mahasiswaList);
        model.addAttribute("nilaiMap", gradebookService.getNilaiGroupedByMahasiswa());
        model.addAttribute("active", "mahasiswa");
        return "mahasiswa";
    }

    @GetMapping("/nilai")
    public String nilai(Model model, HttpSession session) {
        if (!isDosen(session))
            return "redirect:/login";
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
        if (!isDosen(session))
            return "redirect:/login";

        // Validasi nilai
        var validation = validationService.validateNilai(tugas, uts, uas);
        if (!validation.isValid()) {
            redirectAttributes.addFlashAttribute("error", validation.errorMessage());
            return "redirect:/nilai";
        }

        // Check for duplicate nilai
        if (gradebookService.existsNilai(nim, mataKuliah)) {
            redirectAttributes.addFlashAttribute("error",
                    "Nilai untuk mahasiswa dan mata kuliah ini sudah ada! Gunakan fitur Edit untuk mengubah nilai.");
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
        if (!isDosen(session))
            return "redirect:/login";

        gradebookService.findNilaiById(id).ifPresent(nilai -> {
            model.addAttribute("nilai", nilai);
            // Get mahasiswa by nim for display
            if (nilai.getNim() != null) {
                gradebookService.findMahasiswa(nilai.getNim()).ifPresent(mahasiswa -> {
                    model.addAttribute("mahasiswa", mahasiswa);
                });
            }
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
        if (!isDosen(session))
            return "redirect:/login";

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
        if (!isDosen(session))
            return "redirect:/login";
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
        if (!isDosen(session))
            return "redirect:/login";
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

        // Get courses from enrollments
        List<Enrollment> enrollments = enrollmentRepository.findByNim(nim);
        Map<String, Map<String, Object>> coursesMap = new HashMap<>();

        enrollments.stream()
                .filter(e -> "ENROLLED".equals(e.getStatus()) || "COMPLETED".equals(e.getStatus()))
                .forEach(e -> {
                    Map<String, Object> course = new HashMap<>();
                    course.put("id", e.getMataKuliahId());
                    course.put("kode", e.getKodeMataKuliah());
                    course.put("nama", e.getNamaMataKuliah());
                    coursesMap.put(e.getNamaMataKuliah(), course);
                });

        // Also get courses from existing nilai (for backward compatibility)
        List<Nilai> nilaiList = gradebookService.getNilai(nim);
        nilaiList.forEach(nilai -> {
            String mataKuliahNama = nilai.getMataKuliah();
            if (mataKuliahNama != null && !coursesMap.containsKey(mataKuliahNama)) {
                // Try to find mata kuliah by name
                mataKuliahRepository.findAll().stream()
                        .filter(mk -> mk.getNama().equals(mataKuliahNama) || mk.getKode().equals(mataKuliahNama))
                        .findFirst()
                        .ifPresentOrElse(mk -> {
                            Map<String, Object> course = new HashMap<>();
                            course.put("id", mk.getId());
                            course.put("kode", mk.getKode());
                            course.put("nama", mk.getNama());
                            coursesMap.put(mk.getNama(), course);
                        }, () -> {
                            // If not found in mata_kuliah table, create entry from nilai data
                            Map<String, Object> course = new HashMap<>();
                            course.put("id", null);
                            course.put("kode", mataKuliahNama);
                            course.put("nama", mataKuliahNama);
                            coursesMap.put(mataKuliahNama, course);
                        });
            }
        });

        List<Map<String, Object>> courses = new java.util.ArrayList<>(coursesMap.values());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("courses", courses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/{nim}")
    public ResponseEntity<FileSystemResource> downloadReport(@PathVariable String nim, HttpSession session) {
        if (!isDosen(session))
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/login").build();
        File file = gradebookService.generateReportForMahasiswa(nim);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-" + nim + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(new FileSystemResource(file));
    }

    private boolean isDosen(HttpSession session) {
        Object role = session.getAttribute("userRole");
        return role != null && "DOSEN".equals(role.toString());
    }
}