-- Debug Query: Cek semua mahasiswa di database

-- 1. Cek jumlah mahasiswa
SELECT COUNT(*) as total_mahasiswa FROM mahasiswa;

-- 2. Lihat semua data mahasiswa
SELECT nim, nama, username, password FROM mahasiswa ORDER BY nim;

-- 3. Cek apakah ada filter yang salah
SELECT * FROM mahasiswa WHERE username IS NOT NULL;

-- 4. Cek mahasiswa dengan nilai
SELECT 
    m.nim, 
    m.nama, 
    m.username,
    COUNT(n.id) as jumlah_nilai
FROM mahasiswa m
LEFT JOIN nilai n ON m.nim = n.nim
GROUP BY m.nim, m.nama, m.username
ORDER BY m.nim;

