package com.example.tubes_pbo;

import com.example.tubes_pbo.model.MataKuliah;
import com.example.tubes_pbo.repository.MataKuliahRepository;
import com.example.tubes_pbo.repository.EnrollmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CourseController {

    private final MataKuliahRepository mataKuliahRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseController(MataKuliahRepository mataKuliahRepository, EnrollmentRepository enrollmentRepository) {
        this.mataKuliahRepository = mataKuliahRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping("/courses")
    public String coursesPage(Model model, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        
        String kodeDosen = (String) session.getAttribute("refId");
        model.addAttribute("courses", mataKuliahRepository.findByKodeDosen(kodeDosen));
        model.addAttribute("active", "courses");
        return "courses";
    }

    @PostMapping("/courses")
    public String addCourse(@RequestParam String kode,
                            @RequestParam String nama,
                            @RequestParam int sks,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) return "redirect:/login";

        String kodeDosen = (String) session.getAttribute("refId");
        
        // Validation
        if (kode == null || kode.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Kode mata kuliah tidak boleh kosong!");
            return "redirect:/courses";
        }

        if (nama == null || nama.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nama mata kuliah tidak boleh kosong!");
            return "redirect:/courses";
        }

        if (sks < 1 || sks > 6) {
            redirectAttributes.addFlashAttribute("error", "SKS harus antara 1-6!");
            return "redirect:/courses";
        }

        // Check if kode already exists
        if (mataKuliahRepository.findByKode(kode.trim()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Kode mata kuliah sudah digunakan!");
            return "redirect:/courses";
        }

        try {
            MataKuliah mk = new MataKuliah(kode.trim(), nama.trim(), sks, kodeDosen);
            mataKuliahRepository.insert(mk);
            redirectAttributes.addFlashAttribute("success", "Mata kuliah berhasil ditambahkan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambah mata kuliah: " + e.getMessage());
        }

        return "redirect:/courses";
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) return "redirect:/login";

        try {
            // Check if there are enrollments
            long enrollmentCount = enrollmentRepository.findByMataKuliahId(id).size();
            if (enrollmentCount > 0) {
                redirectAttributes.addFlashAttribute("error", 
                    "Tidak dapat menghapus mata kuliah yang sudah memiliki " + enrollmentCount + " mahasiswa terdaftar!");
                return "redirect:/courses";
            }

            mataKuliahRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Mata kuliah berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus mata kuliah: " + e.getMessage());
        }

        return "redirect:/courses";
    }

    @GetMapping("/enrollments")
    public String pendingEnrollments(Model model, HttpSession session) {
        if (!isDosen(session)) return "redirect:/login";
        
        String kodeDosen = (String) session.getAttribute("refId");
        model.addAttribute("pendingEnrollments", enrollmentRepository.findPendingByKodeDosen(kodeDosen));
        model.addAttribute("active", "enrollments");
        return "enrollments";
    }

    @PostMapping("/enrollments/{id}/accept")
    public String acceptEnrollment(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) return "redirect:/login";

        try {
            enrollmentRepository.updateStatus(id, "ENROLLED");
            redirectAttributes.addFlashAttribute("success", "Enrollment berhasil diterima!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menerima enrollment: " + e.getMessage());
        }

        return "redirect:/enrollments";
    }

    @PostMapping("/enrollments/{id}/reject")
    public String rejectEnrollment(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) return "redirect:/login";

        try {
            enrollmentRepository.updateStatus(id, "REJECTED");
            redirectAttributes.addFlashAttribute("success", "Enrollment berhasil ditolak!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menolak enrollment: " + e.getMessage());
        }

        return "redirect:/enrollments";
    }

    private boolean isDosen(HttpSession session) {
        Object role = session.getAttribute("userRole");
        return role != null && "DOSEN".equals(role.toString());
    }
}
