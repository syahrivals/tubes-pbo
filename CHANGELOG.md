# 📋 Changelog - Sistem Gradebook PBO

## 🎉 Version 2.0 - Major Update (2024)

### 🔐 Security Enhancements

#### ✅ Password Encryption dengan BCrypt
- **Added**: `PasswordService.java` untuk handle password encryption/verification
- **Updated**: `AuthService.java` menggunakan BCrypt untuk validasi password
- **Updated**: `GradebookService.java` untuk hash password saat create/update mahasiswa
- **Added**: Database migration `V4__update_passwords.sql` untuk update existing passwords
- **Benefit**: Password tidak lagi disimpan dalam plain text, meningkatkan keamanan sistem

### 👨‍🎓 Fitur Mahasiswa - Complete Overhaul

#### ✅ Enhanced Dashboard dengan Statistik Lengkap
- **Total Mata Kuliah**: Menampilkan jumlah mata kuliah yang diambil
- **Rata-rata Keseluruhan**: Perhitungan otomatis rata-rata semua nilai
- **Nilai Tertinggi & Terendah**: Tracking performa mahasiswa
- **Distribusi Grade**: Visual representation distribusi A, B, C, D, E
- **UI Cards**: Beautiful gradient cards dengan hover animations

#### ✅ Change Password Feature
- **Added**: `MahasiswaRepository.updatePassword()` method
- **Added**: `GradebookService.changePassword()` dengan validation
- **Added**: `StudentController.changePassword()` endpoint
- **Added**: `student-profile.html` template untuk profile & change password
- **Validation**: Password minimal 4 karakter, konfirmasi password harus match
- **Security**: Verifikasi password lama sebelum update

#### ✅ Export Transkrip Nilai ke PDF
- **Added**: `PdfExportService.java` menggunakan iText PDF library
- **Added**: `StudentController.exportPdf()` endpoint
- **Features**:
  - Professional PDF layout dengan header & footer
  - Tabel nilai dengan color-coded grades
  - Informasi mahasiswa lengkap
  - Rata-rata keseluruhan
  - Timestamp cetak dokumen
- **Dependency**: Added `itextpdf:5.5.13.3` to pom.xml

### 🎨 UI/UX Improvements

#### ✅ Modern Design System
- **Gradient Backgrounds**: Purple-blue gradient theme
- **Animated Elements**: 
  - Fade-in animations untuk page load
  - Slide-in animations untuk alerts
  - Hover effects pada cards dan buttons
  - Pulse animation pada hero section
- **Color-coded Grades**:
  - A: Green (Success)
  - B: Blue (Primary)
  - C: Cyan (Info)
  - D: Yellow (Warning)
  - E: Red (Danger)

#### ✅ Enhanced Layout
- **Updated**: `layout.html` dengan modern styling
  - Improved navigation dengan icons
  - Backdrop blur effects
  - Smooth transitions
  - Better responsive design
- **Updated**: `student.html` dengan statistik cards
- **Added**: `student-profile.html` untuk profile management

### 🛡️ Validation & Error Handling

#### ✅ Input Validation
- **Login Form**:
  - Required fields validation
  - Empty string prevention
  - Username trimming
  - Retain username on error
- **Change Password**:
  - Password length validation (min 4 chars)
  - Confirm password matching
  - Old password verification
- **Success/Error Messages**: 
  - Flash attributes untuk feedback
  - Dismissible alerts
  - Color-coded messages

#### ✅ Exception Handling
- **Try-Catch blocks** pada critical operations
- **Graceful error messages** untuk user
- **Redirect handling** untuk unauthorized access

### 📦 Dependencies Added

```xml
<!-- Security & Encryption -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.70</version>
</dependency>

<!-- PDF Generation -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>
```

### 📊 Statistik Perubahan

- **Files Modified**: 12 files
- **Files Added**: 4 new files
  - `PasswordService.java`
  - `PdfExportService.java`
  - `student-profile.html`
  - `V4__update_passwords.sql`
- **Lines of Code**: ~500+ lines added
- **New Features**: 7 major features
- **Bug Fixes**: Multiple validation improvements

---

## 🔄 Version 1.0 - Initial Release

### ✅ Core Features

#### Dosen Module
- Login & Logout
- Dashboard overview
- Manage mahasiswa
- Manage nilai
- Generate reports

#### Mahasiswa Module (Basic)
- Login & Logout
- View nilai
- Calculate average
- View grades

#### Database
- MySQL integration
- Flyway migrations
- Seed data

#### Models (OOP)
- Abstract `Akun` class
- `Dosen` extends `Akun`
- `Mahasiswa` extends `Akun`
- `Nilai` with grade calculation

### 🎨 UI
- Bootstrap 5 layout
- Thymeleaf templates
- Basic gradient design

---

## 📝 Migration Guide

### Updating from v1.0 to v2.0

1. **Update Dependencies**
   ```bash
   mvn clean install
   ```

2. **Database Migration**
   - Flyway akan otomatis run `V4__update_passwords.sql`
   - Existing passwords akan di-hash dengan BCrypt

3. **Testing**
   - Login dengan credentials yang sama (password: `1234`)
   - Test change password feature
   - Test PDF export

4. **Backward Compatibility**
   - ✅ Semua fitur v1.0 tetap berfungsi
   - ✅ Database schema compatible
   - ✅ Existing data preserved

---

## 🚀 What's Next?

### Potential Future Features
- [ ] Email notifications
- [ ] Forgot password functionality
- [ ] Profile picture upload
- [ ] Advanced analytics & charts
- [ ] Export to Excel
- [ ] Multi-semester support
- [ ] Student attendance tracking
- [ ] Discussion forum
- [ ] Assignment submission system

---

**Last Updated**: December 2024

