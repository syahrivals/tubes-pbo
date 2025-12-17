-- Create mata_kuliah table
CREATE TABLE IF NOT EXISTS mata_kuliah (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kode VARCHAR(32) UNIQUE NOT NULL,
    nama VARCHAR(255) NOT NULL,
    sks INT NOT NULL DEFAULT 3,
    kode_dosen VARCHAR(32),
    CONSTRAINT fk_mk_dosen FOREIGN KEY (kode_dosen) REFERENCES dosen(kode_dosen) ON DELETE SET NULL
);

-- Create enrollment table
CREATE TABLE IF NOT EXISTS enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nim VARCHAR(32) NOT NULL,
    mata_kuliah_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ENROLLED',
    enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enrollment_mahasiswa FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_matakuliah FOREIGN KEY (mata_kuliah_id) REFERENCES mata_kuliah(id) ON DELETE CASCADE,
    CONSTRAINT unique_enrollment UNIQUE(nim, mata_kuliah_id)
);

-- Update nilai table - add new columns
ALTER TABLE nilai ADD COLUMN IF NOT EXISTS enrollment_id BIGINT;
ALTER TABLE nilai ADD COLUMN IF NOT EXISTS mata_kuliah_id BIGINT;
ALTER TABLE nilai ADD COLUMN IF NOT EXISTS nim VARCHAR(32);

-- Add foreign keys to nilai table
ALTER TABLE nilai ADD CONSTRAINT IF NOT EXISTS fk_nilai_enrollment 
    FOREIGN KEY (enrollment_id) REFERENCES enrollment(id) ON DELETE CASCADE;
ALTER TABLE nilai ADD CONSTRAINT IF NOT EXISTS fk_nilai_matakuliah 
    FOREIGN KEY (mata_kuliah_id) REFERENCES mata_kuliah(id) ON DELETE CASCADE;

-- Insert sample mata kuliah
INSERT INTO mata_kuliah (kode, nama, sks, kode_dosen) VALUES
('IF101', 'Pemrograman Berorientasi Objek', 3, 'D001'),
('IF102', 'Struktur Data', 3, 'D001'),
('IF103', 'Basis Data', 3, 'D001'),
('IF104', 'Algoritma dan Pemrograman', 4, 'D001'),
('IF105', 'Sistem Operasi', 3, 'D001')
ON DUPLICATE KEY UPDATE nama = VALUES(nama);

-- Migrate old nilai data to new structure (if exists)
-- Find or create mata kuliah for old data
INSERT INTO mata_kuliah (kode, nama, sks, kode_dosen)
SELECT DISTINCT 
    CONCAT('OLD_', REPLACE(REPLACE(mata_kuliah, ' ', '_'), '-', '_')) as kode,
    mata_kuliah as nama,
    3 as sks,
    'D001' as kode_dosen
FROM nilai 
WHERE mata_kuliah IS NOT NULL 
  AND mata_kuliah_id IS NULL
  AND NOT EXISTS (SELECT 1 FROM mata_kuliah WHERE nama = nilai.mata_kuliah)
ON DUPLICATE KEY UPDATE nama = VALUES(nama);

-- Create enrollments for old nilai data
INSERT INTO enrollment (nim, mata_kuliah_id, status, enrolled_at)
SELECT DISTINCT 
    nilai.nim,
    mk.id,
    'COMPLETED' as status,
    NOW() as enrolled_at
FROM nilai
JOIN mata_kuliah mk ON (mk.nama = nilai.mata_kuliah OR mk.kode = nilai.mata_kuliah)
WHERE nilai.nim IS NOT NULL 
  AND nilai.mata_kuliah IS NOT NULL
  AND nilai.enrollment_id IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM enrollment 
      WHERE enrollment.nim = nilai.nim 
        AND enrollment.mata_kuliah_id = mk.id
  )
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Update nilai with enrollment_id and mata_kuliah_id from old data
UPDATE nilai n
JOIN mata_kuliah mk ON (mk.nama = n.mata_kuliah OR mk.kode = n.mata_kuliah)
JOIN enrollment e ON (e.nim = n.nim AND e.mata_kuliah_id = mk.id)
SET n.enrollment_id = e.id,
    n.mata_kuliah_id = mk.id
WHERE n.mata_kuliah IS NOT NULL 
  AND n.enrollment_id IS NULL;

