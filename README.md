# 📚 Sistem Manajemen Nilai Mahasiswa (Gradebook System)

Aplikasi web berbasis Spring Boot untuk manajemen nilai mahasiswa dengan fitur login untuk Dosen dan Mahasiswa.

## ✨ Fitur Utama

### 👨‍🏫 Fitur Dosen
- ✅ Login & Logout sistem
- ✅ Dashboard overview (total mahasiswa, total nilai)
- ✅ Kelola data mahasiswa (tambah mahasiswa baru)
- ✅ Kelola nilai mahasiswa (tambah, hapus nilai)
- ✅ Generate report per mahasiswa
- ✅ View nilai per mata kuliah dengan grade

### 👨‍🎓 Fitur Mahasiswa (BARU & LENGKAP!)
- ✅ Login & Logout sistem
- ✅ Dashboard dengan statistik lengkap:
  - Total mata kuliah
  - Rata-rata keseluruhan
  - Nilai tertinggi & terendah
  - Distribusi grade (A, B, C, D, E)
- ✅ View nilai per mata kuliah (Tugas, UTS, UAS)
- ✅ Lihat rata-rata nilai & grade otomatis
- ✅ **Change Password** (ubah password sendiri)
- ✅ **Export Transkrip Nilai ke PDF**
- ✅ Profile management

## 🔐 Keamanan

- ✅ **Password Encryption** menggunakan BCrypt
- ✅ **Session Management** untuk autentikasi
- ✅ **Role-based Access Control** (Dosen vs Mahasiswa)
- ✅ **Input Validation** pada semua form
- ✅ **Error Handling** yang comprehensive

## 🎨 UI/UX Improvements

- ✅ Modern gradient design dengan animasi
- ✅ Hover effects pada cards dan buttons
- ✅ Responsive layout (Bootstrap 5)
- ✅ Color-coded grades (A=Hijau, B=Biru, C=Cyan, D=Kuning, E=Merah)
- ✅ Smooth animations dengan Animate.css
- ✅ Beautiful statistics cards dengan gradients
- ✅ User-friendly form validation messages

## 📋 Akun Default

### Dosen
- **Username**: `dosen`
- **Password**: `1234`

### Mahasiswa
- **Username**: `budi` | **Password**: `1234` | **NIM**: 220001
- **Username**: `siti` | **Password**: `1234` | **NIM**: 220002

## 🚀 Cara Menjalankan

### Prasyarat
- Java 17+
- MySQL Server
- Maven

### Setup Database
1. Buat database MySQL:
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
```bash
# Compile dan jalankan
mvn spring-boot:run

# Atau compile dulu, lalu run
mvn clean install
java -jar target/tubes-pbo-0.0.1-SNAPSHOT.jar
```

### Akses Aplikasi
Buka browser dan akses: `http://localhost:8080`

## 📊 Teknologi yang Digunakan

- **Backend**: Spring Boot 4.0.0
- **Database**: MySQL + Flyway Migration
- **Template Engine**: Thymeleaf
- **Frontend**: Bootstrap 5 + Animate.css
- **PDF Generation**: iText PDF 5.5.13.3
- **Security**: Spring Security Crypto (BCrypt)
- **Build Tool**: Maven

## 📁 Struktur Project

```
tubes-pbo/
├── src/main/java/com/example/tubes_pbo/
│   ├── model/
│   │   ├── Akun.java                 # Abstract class untuk Dosen & Mahasiswa
│   │   ├── Dosen.java                # Model Dosen
│   │   ├── Mahasiswa.java            # Model Mahasiswa
│   │   └── Nilai.java                # Model Nilai (dengan grade logic)
│   ├── repository/
│   │   ├── DosenRepository.java      # CRUD Dosen
│   │   ├── MahasiswaRepository.java  # CRUD Mahasiswa (+ update password)
│   │   └── NilaiRepository.java      # CRUD Nilai
│   ├── service/
│   │   ├── AuthService.java          # Login authentication service
│   │   ├── GradebookService.java     # Business logic untuk nilai & mahasiswa
│   │   ├── PasswordService.java      # BCrypt password encryption
│   │   └── PdfExportService.java     # PDF generation service
│   ├── report/
│   │   ├── ReportService.java
│   │   └── ReportServiceInterface.java
│   ├── AuthController.java           # Login/Logout endpoints
│   ├── HomeController.java           # Dosen endpoints
│   ├── StudentController.java        # Mahasiswa endpoints
│   └── TubesPboApplication.java      # Main application
│
├── src/main/resources/
│   ├── db/migration/
│   │   ├── V1__create_tables.sql     # Create tables
│   │   ├── V2__seed.sql              # Initial data
│   │   ├── V3__dosen_auth.sql        # Dosen account
│   │   └── V4__update_passwords.sql  # BCrypt passwords
│   ├── templates/
│   │   ├── layout.html               # Base layout with modern UI
│   │   ├── login.html                # Login page
│   │   ├── student.html              # Dashboard mahasiswa
│   │   └── student-profile.html      # Profile & change password
│   └── application.properties        # Configuration
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

3. **Polymorphism**
   - Method `login()` di `Akun` digunakan oleh `Dosen` dan `Mahasiswa`
   - Repository pattern untuk abstraksi database

4. **Abstraction**
   - `ReportServiceInterface` sebagai interface
   - Repository interfaces untuk data access

## 📝 Fitur Grade System

Grade otomatis dihitung berdasarkan rata-rata nilai:
- **A**: >= 85
- **B**: >= 75
- **C**: >= 65
- **D**: >= 55
- **E**: < 55

## 🔄 Alur Kerja

### Dosen
1. Login dengan username `dosen` password `1234`
2. Lihat dashboard dengan overview
3. Tambah mahasiswa baru di menu Mahasiswa
4. Input nilai mahasiswa di menu Nilai
5. Generate report per mahasiswa
6. Logout

### Mahasiswa
1. Login dengan username mahasiswa (budi/siti) password `1234`
2. Lihat dashboard dengan statistik lengkap
3. View nilai per mata kuliah
4. Export transkrip nilai ke PDF
5. Ubah password di menu Profile
6. Logout

## 📄 Export PDF

Mahasiswa dapat mengexport transkrip nilai dengan format profesional yang berisi:
- Informasi mahasiswa (NIM, Nama)
- Daftar nilai per mata kuliah (Tugas, UTS, UAS)
- Grade otomatis dengan color-coding
- Rata-rata keseluruhan
- Tanggal cetak

## 🛡️ Security Features

1. **Password Hashing**: Semua password di-hash menggunakan BCrypt
2. **Session Management**: Menggunakan HttpSession Spring
3. **Authorization**: Setiap endpoint dilindungi dengan role check
4. **Input Validation**: Validasi pada semua input form
5. **SQL Injection Prevention**: Menggunakan Prepared Statements (JdbcTemplate)

## 🎨 Design Highlights

- **Gradient Background**: Modern gradient design
- **Animated Cards**: Hover effects dengan smooth transitions
- **Color-coded Grades**: Visual representation untuk grades
- **Responsive Design**: Mobile-friendly layout
- **Statistics Cards**: Beautiful gradient cards untuk statistik
- **Form Validation**: Real-time validation dengan helpful messages

## 📧 Kontak & Support

Untuk pertanyaan atau issues, silakan buat issue di repository ini.

---

**Dibuat dengan ❤️ menggunakan Spring Boot & Bootstrap 5**

