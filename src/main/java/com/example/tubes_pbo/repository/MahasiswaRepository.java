package com.example.tubes_pbo.repository;

import com.example.tubes_pbo.model.Mahasiswa;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MahasiswaRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Mahasiswa> mapper = (rs, i) -> new Mahasiswa(
            rs.getString("nim"),
            rs.getString("nama"),
            rs.getString("username"),
            rs.getString("password")
    );

    public MahasiswaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mahasiswa> findAll() {
        String sql = "SELECT nim, nama, username, password FROM mahasiswa ORDER BY nim";
        List<Mahasiswa> result = jdbcTemplate.query(sql, mapper);
        System.out.println("=== REPOSITORY DEBUG ===");
        System.out.println("SQL: " + sql);
        System.out.println("Total rows returned: " + result.size());
        result.forEach(m -> System.out.println("  - NIM: " + m.getNim() + ", Nama: " + m.getNama()));
        return result;
    }

    public Optional<Mahasiswa> findByNim(String nim) {
        List<Mahasiswa> list = jdbcTemplate.query("select nim, nama, username, password from mahasiswa where nim = ?", mapper, nim);
        return list.stream().findFirst();
    }

    public Optional<Mahasiswa> findByUsername(String username) {
        List<Mahasiswa> list = jdbcTemplate.query("select nim, nama, username, password from mahasiswa where username = ?", mapper, username);
        return list.stream().findFirst();
    }

    public void insert(Mahasiswa m) {
        jdbcTemplate.update(
                "insert into mahasiswa(nim, nama, username, password) values(?, ?, ?, ?)",
                m.getNim(), m.getNama(), m.getUsername(), m.getPassword()
        );
    }

    public long count() {
        Long c = jdbcTemplate.queryForObject("select count(*) from mahasiswa", Long.class);
        return c == null ? 0 : c;
    }

    public boolean updatePassword(String nim, String newPassword) {
        int rows = jdbcTemplate.update("update mahasiswa set password = ? where nim = ?", newPassword, nim);
        return rows > 0;
    }

    public boolean update(String nim, String nama, String username) {
        int rows = jdbcTemplate.update(
                "update mahasiswa set nama = ?, username = ? where nim = ?",
                nama, username, nim
        );
        return rows > 0;
    }

    public boolean deleteByNim(String nim) {
        int rows = jdbcTemplate.update("DELETE FROM mahasiswa WHERE nim = ?", nim);
        return rows > 0;
    }
}

