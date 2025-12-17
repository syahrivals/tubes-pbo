create table if not exists mahasiswa (
    nim varchar(32) primary key,
    nama varchar(255) not null,
    username varchar(255),
    password varchar(255)
);

create table if not exists nilai (
    id bigint auto_increment primary key,
    nim varchar(32) not null,
    mata_kuliah varchar(255) not null,
    tugas double not null,
    uts double not null,
    uas double not null,
    constraint fk_nilai_mahasiswa foreign key (nim) references mahasiswa(nim) on delete cascade
);

