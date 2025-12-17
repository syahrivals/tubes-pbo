-- Drop existing tables if any
DROP TABLE IF EXISTS nilai;
DROP TABLE IF EXISTS mahasiswa;
DROP TABLE IF EXISTS dosen;

-- Create tables
CREATE TABLE IF NOT EXISTS mahasiswa (
    nim VARCHAR(32) PRIMARY KEY,
    nama VARCHAR(255) NOT NULL,
    username VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS nilai (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nim VARCHAR(32) NOT NULL,
    mata_kuliah VARCHAR(255) NOT NULL,
    tugas DOUBLE NOT NULL,
    uts DOUBLE NOT NULL,
    uas DOUBLE NOT NULL,
    CONSTRAINT fk_nilai_mahasiswa FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS dosen (
    kode_dosen VARCHAR(32) PRIMARY KEY,
    nama VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Insert data dosen
INSERT INTO dosen (kode_dosen, nama, username, password) VALUES
('D001', 'Admin Dosen', 'dosen', '1234');

-- Insert data mahasiswa
INSERT INTO mahasiswa (nim, nama, username, password) VALUES
('220001', 'Budi Santoso', 'budi', '1234'),
('220002', 'Siti Aminah', 'siti', '1234');

-- Insert data nilai
INSERT INTO nilai (nim, mata_kuliah, tugas, uts, uas) VALUES
('220001', 'PBO', 80, 85, 88),
('220001', 'Struktur Data', 78, 80, 82),
('220002', 'PBO', 90, 92, 94);

-- Verify
SELECT 'Dosen' as tabel, COUNT(*) as jumlah FROM dosen
UNION ALL
SELECT 'Mahasiswa', COUNT(*) FROM mahasiswa
UNION ALL
SELECT 'Nilai', COUNT(*) FROM nilai;

