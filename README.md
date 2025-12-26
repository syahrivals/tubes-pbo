# 📚 Sistem Manajemen Nilai Mahasiswa (Gradebook System)

Aplikasi web berbasis Spring Boot untuk manajemen nilai mahasiswa dengan fitur login untuk Dosen dan Mahasiswa.

## ✨ Fitur Utama

### 👨‍🏫 Fitur Dosen
- ✅ Login & Logout sistem
- ✅ Dashboard overview dengan statistik lengkap:
  - Total mahasiswa, mata kuliah, dan entri nilai
  - Chart distribusi grade (Doughnut Chart)
  - Chart statistik overview (Bar Chart)
  - Daftar nilai mahasiswa dengan grade cards
- ✅ Kelola data mahasiswa (tambah mahasiswa baru)
- ✅ Kelola mata kuliah dan enrollment requests
- ✅ Kelola nilai mahasiswa (tambah, edit, hapus nilai)
- ✅ Generate report per mahasiswa
- ✅ View nilai per mata kuliah dengan grade
- ✅ Activity logs dan notifications

### 👨‍🎓 Fitur Mahasiswa
- ✅ Login & Logout sistem
- ✅ Dashboard dengan statistik lengkap:
  - Total mata kuliah
  - Rata-rata keseluruhan
  - Nilai tertinggi & terendah
  - Distribusi grade (A, B, C, D, E)
- ✅ Browse katalog mata kuliah
- ✅ Enrollment ke mata kuliah (dengan approval system)
- ✅ View nilai per mata kuliah (Tugas, UTS, UAS)
- ✅ Lihat rata-rata nilai & grade otomatis
- ✅ **Change Password** (ubah password sendiri)
- ✅ **Export Transkrip Nilai ke PDF**
- ✅ Profile management
- ✅ Notifications system

## 🔐 Keamanan

- ✅ **Password Encryption** menggunakan BCrypt
- ✅ **Session Management** untuk autentikasi
- ✅ **Role-based Access Control** (Dosen vs Mahasiswa)
- ✅ **Input Validation** pada semua form
- ✅ **Error Handling** yang comprehensive
- ✅ **SQL Injection Prevention** menggunakan Prepared Statements (JdbcTemplate)

## 🎨 UI/UX Improvements

### Modern Dark Theme
- ✅ **Tailwind CSS** untuk utility-first styling
- ✅ **Dark Theme** dengan high contrast (WCAG AA+ compliant)
- ✅ **Color Theory** diterapkan untuk readability yang optimal
- ✅ **Gradient Design** untuk visual appeal
- ✅ **Smooth Animations** dengan fade-in effects
- ✅ **Responsive Layout** untuk semua device

### Navigation & Layout
- ✅ **Clean Navbar** dengan spacing yang optimal
- ✅ **Sticky Navigation** untuk easy access
- ✅ **Active State Indicators** yang jelas
- ✅ **Mobile Responsive** dengan toggle menu

### Dashboard Features
- ✅ **Interactive Charts** menggunakan Chart.js:
  - Doughnut chart untuk distribusi grade
  - Bar chart untuk statistik overview
- ✅ **Statistics Cards** dengan gradient backgrounds
- ✅ **Grade Cards** dengan color-coded badges
- ✅ **Hover Effects** pada semua interactive elements

### Color System
- ✅ **Grade Color Coding**:
  - A: Green gradient (#10b981)
  - B: Primary/Indigo gradient (#6366f1)
  - C: Cyan gradient (#06b6d4)
  - D: Yellow/Orange gradient (#eab308)
  - E: Red gradient (#ef4444)
- ✅ **High Contrast Text** untuk accessibility
- ✅ **Consistent Color Palette** di seluruh aplikasi

## 📋 Akun Default

### Dosen
- **Username**: `dosen`
- **Password**: `1234`

### Mahasiswa
- **Username**: `budi` | **Password**: `1234` | **NIM**: 220001
- **Username**: `siti` | **Password**: `1234` | **NIM**: 220002

## 🚀 Cara Menjalankan

### Prasyarat
- **Java 17+** (disarankan menggunakan JDK 17)
- **MySQL Server** (versi 8.0 atau lebih baru)
- **Maven** (terintegrasi dengan Maven Wrapper)

### Setup Database

Database akan otomatis dibuat oleh Flyway Migration saat aplikasi pertama kali dijalankan. Pastikan MySQL server sudah berjalan.

1. Buat database MySQL (opsional, bisa dibuat otomatis):
```sql
CREATE DATABASE gradebook;
```

2. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gradebook
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Menjalankan Aplikasi

#### Windows
```powershell
# Set JAVA_HOME jika belum (contoh untuk JDK 17)
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot"

# Compile dan jalankan
.\mvnw.cmd spring-boot:run

# Atau compile dulu, lalu run
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

#### Linux/Mac
```bash
# Compile dan jalankan
./mvnw spring-boot:run

# Atau compile dulu, lalu run
./mvnw clean install
java -jar target/tubes-pbo-0.0.1-SNAPSHOT.jar
```

### Akses Aplikasi
Buka browser dan akses: `http://localhost:8080`

## 📊 Teknologi yang Digunakan

### Backend
- **Spring Boot 4.0.0** - Framework utama
- **MySQL** - Database
- **Flyway Migration** - Database versioning & migration
- **Thymeleaf** - Template engine
- **Spring Security Crypto (BCrypt)** - Password hashing
- **iText PDF 5.5.13.3** - PDF generation
- **Maven** - Build tool & dependency management

### Frontend
- **Tailwind CSS** - Utility-first CSS framework (via CDN)
- **Bootstrap Icons** - Icon library
- **Chart.js 4.4.0** - Interactive charts
- **Custom CSS** - Dark theme & custom styling

## 📁 Struktur Project

```
tubes-pbo/
├── src/main/java/com/example/tubes_pbo/
│   ├── model/
│   │   ├── Akun.java                 # Abstract class untuk Dosen & Mahasiswa
│   │   ├── Dosen.java                # Model Dosen
│   │   ├── Mahasiswa.java            # Model Mahasiswa
│   │   ├── Nilai.java                # Model Nilai (dengan grade logic)
│   │   ├── MataKuliah.java           # Model Mata Kuliah
│   │   ├── Semester.java             # Model Semester
│   │   ├── Enrollment.java           # Model Enrollment
│   │   ├── Notifikasi.java           # Model Notifikasi
│   │   └── ActivityLog.java          # Model Activity Log
│   ├── repository/
│   │   ├── DosenRepository.java      # CRUD Dosen
│   │   ├── MahasiswaRepository.java  # CRUD Mahasiswa (+ update password)
│   │   ├── NilaiRepository.java      # CRUD Nilai
│   │   ├── MataKuliahRepository.java # CRUD Mata Kuliah
│   │   ├── EnrollmentRepository.java  # CRUD Enrollment
│   │   ├── NotifikasiRepository.java # CRUD Notifikasi
│   │   └── ActivityLogRepository.java # CRUD Activity Log
│   ├── service/
│   │   ├── AuthService.java          # Login authentication service
│   │   ├── GradebookService.java     # Business logic untuk nilai & mahasiswa
│   │   ├── PasswordService.java      # BCrypt password encryption
│   │   ├── PdfExportService.java     # PDF generation service
│   │   ├── NotifikasiService.java   # Notification service
│   │   ├── LogService.java           # Activity logging service
│   │   └── ValidationService.java   # Input validation service
│   ├── report/
│   │   ├── ReportService.java
│   │   └── ReportServiceInterface.java
│   ├── AuthController.java           # Login/Logout endpoints
│   ├── HomeController.java           # Dosen endpoints (dashboard, mahasiswa, nilai, logs)
│   ├── StudentController.java        # Mahasiswa endpoints (dashboard, profile, courses)
│   ├── CourseController.java         # Course management endpoints
│   ├── RegisterController.java       # Student registration endpoints
│   └── TubesPboApplication.java      # Main application
│
├── src/main/resources/
│   ├── db/migration/
│   │   ├── V1__create_tables.sql     # Create tables
│   │   ├── V2__seed.sql              # Initial data
│   │   ├── V3__dosen_auth.sql        # Dosen account
│   │   └── V4__update_passwords.sql  # BCrypt passwords
│   ├── templates/
│   │   ├── layout.html               # Base layout dengan Tailwind CSS
│   │   ├── login.html                # Login page
│   │   ├── register.html             # Registration page
│   │   ├── index.html                # Dosen dashboard
│   │   ├── mahasiswa.html            # Student management
│   │   ├── nilai.html                # Grade management
│   │   ├── courses.html              # Course management
│   │   ├── enrollments.html          # Enrollment requests
│   │   ├── logs.html                 # Activity logs
│   │   ├── notifications.html        # Notifications
│   │   ├── student.html              # Student dashboard
│   │   ├── student-profile.html      # Profile & change password
│   │   ├── student-browse-courses.html # Course catalog
│   │   └── student-enrollments.html  # Student enrollments
│   ├── static/css/
│   │   └── main.css                  # Custom CSS untuk dark theme
│   ├── application.properties        # Configuration
│   └── messages.properties           # i18n (Indonesian & English)
│
└── pom.xml                           # Maven dependencies
```

## 🎯 Konsep OOP yang Diimplementasikan

1. **Inheritance (Pewarisan)**
   - `Akun` sebagai abstract class
   - `Dosen` extends `Akun`
   - `Mahasiswa` extends `Akun`

2. **Encapsulation**
   - Private fields dengan getter/setter
   - Protected constructor di `Akun`
   - Data hiding untuk keamanan

3. **Polymorphism**
   - Method `login()` di `Akun` digunakan oleh `Dosen` dan `Mahasiswa`
   - Repository pattern untuk abstraksi database
   - Interface `ReportServiceInterface` untuk report generation

4. **Abstraction**
   - `ReportServiceInterface` sebagai interface
   - Repository interfaces untuk data access
   - Service layer untuk business logic abstraction

## 📝 Fitur Grade System

Grade otomatis dihitung berdasarkan rata-rata nilai:
- **A**: >= 85 (Green gradient)
- **B**: >= 75 (Indigo/Primary gradient)
- **C**: >= 65 (Cyan gradient)
- **D**: >= 55 (Yellow/Orange gradient)
- **E**: < 55 (Red gradient)

Formula: `Rata-rata = (Tugas × 0.3) + (UTS × 0.35) + (UAS × 0.35)`

## 🔄 Alur Kerja

### Dosen
1. Login dengan username `dosen` password `1234`
2. Lihat dashboard dengan overview statistik dan charts
3. Kelola mata kuliah di menu MK
4. Approve/reject enrollment requests di menu Enrollment
5. Tambah mahasiswa baru di menu Mahasiswa
6. Input nilai mahasiswa di menu Nilai
7. Generate report per mahasiswa
8. Lihat activity logs di menu Logs
9. Logout

### Mahasiswa
1. Register akun baru atau login dengan username mahasiswa (budi/siti) password `1234`
2. Lihat dashboard dengan statistik lengkap
3. Browse katalog mata kuliah di menu Katalog
4. Enroll ke mata kuliah (menunggu approval dosen)
5. View nilai per mata kuliah di menu MK Saya
6. Export transkrip nilai ke PDF
7. Ubah password di menu Profile
8. Lihat notifications
9. Logout

## 📄 Export PDF

Mahasiswa dapat mengexport transkrip nilai dengan format profesional yang berisi:
- Informasi mahasiswa (NIM, Nama)
- Daftar nilai per mata kuliah (Tugas, UTS, UAS)
- Grade otomatis dengan color-coding
- Rata-rata keseluruhan
- Tanggal cetak

## 🛡️ Security Features

1. **Password Hashing**: Semua password di-hash menggunakan BCrypt dengan salt rounds
2. **Session Management**: Menggunakan HttpSession Spring untuk state management
3. **Authorization**: Setiap endpoint dilindungi dengan role check (DOSEN vs MAHASISWA)
4. **Input Validation**: Validasi pada semua input form (NIM, email, password, dll)
5. **SQL Injection Prevention**: Menggunakan Prepared Statements (JdbcTemplate)
6. **XSS Protection**: Thymeleaf auto-escapes output

## 🎨 Design Highlights

### Dark Theme
- **Slate Color Palette**: Menggunakan Tailwind slate colors untuk konsistensi
- **High Contrast**: Text dengan kontras tinggi untuk readability (WCAG AA+)
- **Gradient Accents**: Primary colors dengan gradient untuk visual appeal

### Components
- **Statistics Cards**: Gradient cards dengan hover effects
- **Grade Badges**: Color-coded badges dengan gradient backgrounds
- **Interactive Charts**: Chart.js dengan dark theme configuration
- **Form Elements**: Styled inputs dengan focus states
- **Navigation**: Clean navbar dengan optimal spacing

### Responsive Design
- **Mobile First**: Design dimulai dari mobile viewport
- **Breakpoints**: Responsive pada semua screen sizes
- **Touch Friendly**: Button sizes dan spacing optimal untuk touch devices

## 🔧 Development Notes

### Database Migration
- Menggunakan Flyway untuk database versioning
- Migration files di `src/main/resources/db/migration/`
- Otomatis dijalankan saat aplikasi start

### Internationalization (i18n)
- Support untuk Bahasa Indonesia dan English
- Message files: `messages.properties` dan `messages_id.properties`

### Performance
- Connection pooling untuk database
- Efficient queries dengan JdbcTemplate
- Caching untuk static resources

## 📧 Kontak & Support

Untuk pertanyaan atau issues, silakan buat issue di repository ini.

---

**Dibuat dengan ❤️ menggunakan Spring Boot, Tailwind CSS & Chart.js**

**Version**: 1.0.0  
**Last Updated**: December 2024
