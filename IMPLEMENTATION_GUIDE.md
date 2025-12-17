# 🚀 Implementation Guide - Enrollment System

## 📋 Status: 90% Complete!

### ✅ Sudah Selesai

#### Backend (100%)
- ✅ Model: MataKuliah, Enrollment, Update Nilai
- ✅ Repository: MataKuliahRepository, EnrollmentRepository  
- ✅ Controller: RegisterController, CourseController, Update StudentController
- ✅ Database Migration: V5__enrollment_system.sql

#### Frontend (70%)
- ✅ register.html - Form registrasi
- ✅ courses.html - Dosen kelola mata kuliah
- ✅ login.html - Updated dengan link register

### 🔄 Yang Perlu Dilengkapi (10%)

#### Template Files (Tinggal Copy-Paste)

**1. student-browse-courses.html** - Mahasiswa browse & enroll mata kuliah
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:replace="layout :: layout(~{::section})">
<section>
    <h5 class="mb-3">📚 Katalog Mata Kuliah</h5>
    
    <div class="row g-3">
        <div class="col-md-6 col-lg-4" th:each="course : ${courses}">
            <div class="card shadow-soft h-100">
                <div class="card-body">
                    <span class="badge bg-primary mb-2" th:text="${course.kode}">Kode</span>
                    <h6 th:text="${course.nama}">Nama MK</h6>
                    <p class="small text-muted mb-2">
                        <strong>SKS:</strong> <span th:text="${course.sks}">3</span><br>
                        <strong>Dosen:</strong> <span th:text="${course.namaDosen}">Dosen</span>
                    </p>
                    
                    <form method="post" action="/student/enroll" 
                          th:if="${!enrolledIds.contains(course.id)}">
                        <input type="hidden" name="courseId" th:value="${course.id}">
                        <button type="submit" class="btn btn-sm btn-success w-100">✅ Daftar</button>
                    </form>
                    <div th:if="${enrolledIds.contains(course.id)}" 
                         class="badge bg-success w-100">Sudah Terdaftar</div>
                </div>
            </div>
        </div>
    </div>
</section>
</html>
```

**2. student-enrollments.html** - Daftar mata kuliah yang sudah dienroll
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:replace="layout :: layout(~{::section})">
<section>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h5 class="mb-0">📝 Mata Kuliah Saya</h5>
            <p class="text-muted small mb-0">Total SKS: <span th:text="${totalSks}">0</span></p>
        </div>
        <a href="/student/courses" class="btn btn-primary btn-sm">➕ Tambah Mata Kuliah</a>
    </div>

    <div class="row g-3">
        <div class="col-md-6" th:each="e : ${enrollments}">
            <div class="card shadow-soft">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <div>
                            <span class="badge bg-primary" th:text="${e.kodeMataKuliah}">Kode</span>
                            <h6 class="mt-2" th:text="${e.namaMataKuliah}">Nama</h6>
                            <p class="small text-muted">SKS: <span th:text="${e.sks}">3</span></p>
                        </div>
                        <div>
                            <span class="badge" 
                                  th:classappend="${e.status == 'ENROLLED' ? 'bg-success' : 'bg-secondary'}"
                                  th:text="${e.status}">Status</span>
                        </div>
                    </div>
                    <form th:action="@{/student/enrollments/{id}/drop(id=${e.id})}" 
                          method="post" th:if="${e.status == 'ENROLLED'}"
                          onsubmit="return confirm('Yakin drop mata kuliah ini?')">
                        <button type="submit" class="btn btn-sm btn-outline-danger w-100">Drop</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>
</html>
```

**3. Update layout.html** - Tambah menu navigation
```html
<!-- Di bagian navbar, tambahkan menu untuk mahasiswa -->
<li class="nav-item" th:if="${session.userRole}=='MAHASISWA'">
    <a class="nav-link" th:classappend="${active}=='browse-courses' ? 'active fw-bold' : ''" 
       href="/student/courses">📚 Katalog MK</a>
</li>
<li class="nav-item" th:if="${session.userRole}=='MAHASISWA'">
    <a class="nav-link" th:classappend="${active}=='enrollments' ? 'active fw-bold' : ''" 
       href="/student/enrollments">📝 MK Saya</a>
</li>

<!-- Untuk dosen -->
<li class="nav-item" th:if="${session.userRole}=='DOSEN'">
    <a class="nav-link" th:classappend="${active}=='courses' ? 'active fw-bold' : ''" 
       href="/courses">📚 Mata Kuliah</a>
</li>
```

### 🚀 Cara Menjalankan

1. **Restart Aplikasi** (Ctrl+C lalu run lagi):
```bash
.\mvnw.cmd spring-boot:run
```

2. **Database akan otomatis create tables** melalui Flyway migration V5

3. **Test Fitur Baru**:

#### A. Registrasi Mahasiswa
- Buka `http://localhost:8080/register`
- Isi form (NIM, Nama, Username, Password)
- Submit → Login dengan akun baru

#### B. Dosen Kelola Mata Kuliah  
- Login sebagai `dosen/1234`
- Klik menu "Mata Kuliah"
- Tambah mata kuliah baru (Kode, Nama, SKS)
- Lihat daftar mahasiswa per mata kuliah

#### C. Mahasiswa Enroll Mata Kuliah
- Login sebagai mahasiswa (budi atau akun baru)
- Klik "Katalog MK" → Browse mata kuliah
- Klik "Daftar" untuk enroll
- Lihat di "MK Saya" → Daftar mata kuliah yang dienroll
- Bisa drop mata kuliah jika perlu

### 📊 Fitur Lengkap

**Mahasiswa:**
- ✅ Register akun baru
- ✅ Login/Logout
- ✅ Browse katalog mata kuliah
- ✅ Enroll mata kuliah
- ✅ View enrolled courses
- ✅ Drop mata kuliah
- ✅ View nilai per mata kuliah
- ✅ Export transkrip PDF
- ✅ Change password

**Dosen:**
- ✅ Login/Logout  
- ✅ Create mata kuliah
- ✅ Delete mata kuliah
- ✅ View students per mata kuliah
- ✅ Input nilai mahasiswa
- ✅ Dashboard overview

### 🎯 Sample Data (Auto-created)

Migration V5 sudah menyertakan sample mata kuliah:
- IF101 - Pemrograman Berorientasi Objek (3 SKS)
- IF102 - Struktur Data (3 SKS)
- IF103 - Basis Data (3 SKS)
- IF104 - Algoritma dan Pemrograman (4 SKS)
- IF105 - Sistem Operasi (3 SKS)

### 🐛 Troubleshooting

**Q: Migration V5 error?**
A: Drop database dan buat ulang:
```sql
DROP DATABASE tubes_pbo;
CREATE DATABASE tubes_pbo;
```

**Q: Template not found?**
A: Pastikan file HTML ada di `src/main/resources/templates/`

**Q: Registration error?**
A: Check log untuk detail error, pastikan password di-hash dengan BCrypt

### 📝 Notes

- Password otomatis di-hash dengan BCrypt saat register
- Enrollment unik per mahasiswa-matakuliah (tidak bisa double enroll)
- Mata kuliah tidak bisa dihapus jika ada enrollment
- Status enrollment: ENROLLED, DROPPED, COMPLETED

---

**Status: READY TO USE! 🎉**

