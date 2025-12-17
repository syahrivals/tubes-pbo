-- Create notifikasi table
CREATE TABLE IF NOT EXISTS notifikasi (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nim VARCHAR(32) NOT NULL,
    pesan TEXT NOT NULL,
    tipe VARCHAR(20) NOT NULL DEFAULT 'INFO',
    dibaca BOOLEAN NOT NULL DEFAULT false,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifikasi_mahasiswa FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE
);

-- Create activity_log table
CREATE TABLE IF NOT EXISTS activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_role VARCHAR(20) NOT NULL,
    username VARCHAR(255) NOT NULL,
    action VARCHAR(20) NOT NULL,
    entity_type VARCHAR(50),
    entity_id VARCHAR(255),
    description TEXT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_role (user_role),
    INDEX idx_entity_type (entity_type),
    INDEX idx_timestamp (timestamp)
);

-- Create semester table
CREATE TABLE IF NOT EXISTS semester (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kode VARCHAR(20) UNIQUE NOT NULL,
    nama VARCHAR(255) NOT NULL,
    aktif BOOLEAN NOT NULL DEFAULT false
);

-- Insert default semester
INSERT INTO semester (kode, nama, aktif) VALUES
('2024-1', 'Semester Ganjil 2024/2025', true),
('2024-2', 'Semester Genap 2024/2025', false),
('2023-1', 'Semester Ganjil 2023/2024', false)
ON DUPLICATE KEY UPDATE nama = VALUES(nama);

-- Add semester_id to nilai table (optional, for future use)
ALTER TABLE nilai ADD COLUMN IF NOT EXISTS semester_id BIGINT;
ALTER TABLE nilai ADD CONSTRAINT IF NOT EXISTS fk_nilai_semester 
    FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE SET NULL;

-- Add semester_id to enrollment table
ALTER TABLE enrollment ADD COLUMN IF NOT EXISTS semester_id BIGINT;
ALTER TABLE enrollment ADD CONSTRAINT IF NOT EXISTS fk_enrollment_semester 
    FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE SET NULL;

