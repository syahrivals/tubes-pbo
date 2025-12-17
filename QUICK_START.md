# 🚀 Quick Start Guide - Gradebook System

## ⚡ Langkah Cepat (5 Menit)

### 1️⃣ Setup Database
```sql
CREATE DATABASE gradebook;
```

### 2️⃣ Konfigurasi Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gradebook
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3️⃣ Jalankan Aplikasi
```bash
mvn spring-boot:run
```

### 4️⃣ Akses Browser
```
http://localhost:8080
```

---

## 🔑 Login Credentials

### 👨‍🏫 Dosen
```
Username: dosen
Password: 1234
```

### 👨‍🎓 Mahasiswa
```
Username: budi   (NIM: 220001)
Password: 1234

Username: siti   (NIM: 220002)
Password: 1234
```

---

## 🎯 Testing Fitur

### ✅ Test Fitur Dosen
1. Login sebagai dosen
2. View dashboard → Lihat statistik
3. Klik "Mahasiswa" → Tambah mahasiswa baru
4. Klik "Nilai" → Input nilai mahasiswa
5. Download report mahasiswa
6. Logout

### ✅ Test Fitur Mahasiswa
1. Login sebagai budi/siti
2. View dashboard → Lihat statistik lengkap
3. Lihat nilai per mata kuliah
4. Klik "Export PDF" → Download transkrip
5. Klik "Profile" → Ubah password
6. Logout → Login lagi dengan password baru

---

## 🐛 Troubleshooting

### Problem: MySQL Connection Failed
**Solution**: 
- Pastikan MySQL server sudah running
- Cek username & password di `application.properties`
- Cek database `gradebook` sudah dibuat

### Problem: Port 8080 Already in Use
**Solution**:
```properties
# Tambahkan di application.properties
server.port=8081
```

### Problem: Flyway Migration Error
**Solution**:
```sql
-- Reset database
DROP DATABASE gradebook;
CREATE DATABASE gradebook;
```
Lalu run aplikasi lagi.

### Problem: Cannot find BCryptPasswordEncoder
**Solution**:
```bash
mvn clean install -U
```

---

## 📸 Screenshot Fitur

### Dashboard Mahasiswa
- ✅ Statistik Cards (Total MK, Rata-rata, Tertinggi, Terendah)
- ✅ Distribusi Grade (A, B, C, D, E)
- ✅ Nilai per Mata Kuliah dengan color-coded grades
- ✅ Export PDF button

### Profile Page
- ✅ Informasi Profil (NIM, Nama, Username)
- ✅ Form Change Password
- ✅ Menu Cepat (Dashboard, Export PDF, Logout)

### PDF Export
- ✅ Professional layout
- ✅ Tabel nilai lengkap
- ✅ Color-coded grades
- ✅ Rata-rata keseluruhan
- ✅ Timestamp cetak

---

## 💡 Tips

1. **First Time Setup**: 
   - Flyway akan otomatis create tables & seed data
   - Password sudah di-hash dengan BCrypt

2. **Testing Change Password**:
   - Login sebagai budi
   - Ubah password ke "newpass"
   - Logout dan login dengan password baru

3. **Testing PDF Export**:
   - Login sebagai mahasiswa
   - Klik tombol "Export PDF"
   - File akan otomatis download

4. **Development Mode**:
   - Hot reload: Gunakan `spring-boot-devtools`
   - Debug: Run dengan `-Ddebug` flag

---

## 🎓 Demo Flow

### Complete Demo (10 menit)

**Sebagai Dosen** (5 menit):
1. Login → `dosen/1234`
2. Dashboard → Lihat overview
3. Tambah mahasiswa → `220003, Ahmad`
4. Input nilai → Pilih Ahmad, input nilai
5. Generate report → Download report Ahmad
6. Logout

**Sebagai Mahasiswa** (5 menit):
1. Login → `budi/1234`
2. Dashboard → Lihat statistik
   - Total: 2 MK
   - Rata-rata: 81.67
   - Distribusi: 1A, 1B
3. Export PDF → Download transkrip
4. Profile → Ubah password ke `12345`
5. Logout
6. Login lagi → `budi/12345` ✅

---

## 📞 Need Help?

Jika ada masalah:
1. Check log di console
2. Verify database connection
3. Check `application.properties`
4. Run `mvn clean install`
5. Restart aplikasi

---

**Selamat Mencoba! 🎉**

