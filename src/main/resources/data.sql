delete from nilai;
delete from mahasiswa;

insert into mahasiswa (nim, nama, username, password) values
('220001', 'Budi Santoso', 'budi', '1234'),
('220002', 'Siti Aminah', 'siti', '1234');

insert into nilai (nim, mata_kuliah, tugas, uts, uas) values
('220001', 'PBO', 80, 85, 88),
('220001', 'Struktur Data', 78, 80, 82),
('220002', 'PBO', 90, 92, 94);

