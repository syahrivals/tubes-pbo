-- Update passwords to BCrypt hashed versions
-- Original password for all: "1234"
-- BCrypt hash: $2a$10$X5wFWMnOVvQqMrXXiHd5qOZKxbXgDDQvJLcLZKxEYDKyPZCzXpEiO

UPDATE dosen SET password = '$2a$10$X5wFWMnOVvQqMrXXiHd5qOZKxbXgDDQvJLcLZKxEYDKyPZCzXpEiO' WHERE username = 'dosen';

UPDATE mahasiswa SET password = '$2a$10$X5wFWMnOVvQqMrXXiHd5qOZKxbXgDDQvJLcLZKxEYDKyPZCzXpEiO' WHERE username = 'budi';
UPDATE mahasiswa SET password = '$2a$10$X5wFWMnOVvQqMrXXiHd5qOZKxbXgDDQvJLcLZKxEYDKyPZCzXpEiO' WHERE username = 'siti';

