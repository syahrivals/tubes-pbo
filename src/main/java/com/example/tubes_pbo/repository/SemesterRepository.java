package com.example.tubes_pbo.repository;

import com.example.tubes_pbo.model.Semester;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class SemesterRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Semester> mapper = (rs, i) -> {
        Semester s = new Semester();
        s.setId(rs.getLong("id"));
        s.setKode(rs.getString("kode"));
        s.setNama(rs.getString("nama"));
        s.setAktif(rs.getBoolean("aktif"));
        return s;
    };

    public SemesterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Semester> findAll() {
        return jdbcTemplate.query("SELECT * FROM semester ORDER BY kode DESC", mapper);
    }

    public Optional<Semester> findById(Long id) {
        List<Semester> list = jdbcTemplate.query("SELECT * FROM semester WHERE id = ?", mapper, id);
        return list.stream().findFirst();
    }

    public Optional<Semester> findByKode(String kode) {
        List<Semester> list = jdbcTemplate.query("SELECT * FROM semester WHERE kode = ?", mapper, kode);
        return list.stream().findFirst();
    }

    public Optional<Semester> findAktif() {
        List<Semester> list = jdbcTemplate.query("SELECT * FROM semester WHERE aktif = true LIMIT 1", mapper);
        return list.stream().findFirst();
    }

    public Semester insert(Semester semester) {
        String sql = "INSERT INTO semester(kode, nama, aktif) VALUES(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, semester.getKode());
            ps.setString(2, semester.getNama());
            ps.setBoolean(3, semester.isAktif());
            return ps;
        }, keyHolder);
        
        semester.setId(keyHolder.getKey().longValue());
        return semester;
    }

    public boolean setAktif(Long id) {
        // Set semua menjadi tidak aktif dulu
        jdbcTemplate.update("UPDATE semester SET aktif = false");
        // Set yang dipilih menjadi aktif
        return jdbcTemplate.update("UPDATE semester SET aktif = true WHERE id = ?", id) > 0;
    }
}

