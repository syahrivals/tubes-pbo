create table if not exists dosen (
    kode_dosen varchar(32) primary key,
    nama varchar(255) not null,
    username varchar(255) unique not null,
    password varchar(255) not null
);

insert into dosen (kode_dosen, nama, username, password) values
('D001', 'Admin Dosen', 'dosen', '1234')
on duplicate key update nama = values(nama), password = values(password);

