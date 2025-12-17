package com.example.tubes_pbo.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.tubes_pbo.model.Notifikasi;

@Repository
public class NotifikasiRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Notifikasi> mapper = (rs, i) -> {
        Notifikasi n = new Notifikasi();
        n.setId(rs.getLong("id"));
        n.setNim(rs.getString("nim"));
        n.setKodeDosen(rs.getString("kode_dosen"));
        n.setPesan(rs.getString("pesan"));
        n.setTipe(rs.getString("tipe"));
        n.setDibaca(rs.getBoolean("dibaca"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            n.setCreatedAt(ts.toLocalDateTime());
        }
        return n;
    };

    public NotifikasiRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Notifikasi> findByNim(String nim) {
        return jdbcTemplate.query(
                "SELECT * FROM notifikasi WHERE nim = ? ORDER BY created_at DESC LIMIT 20",
                mapper, nim
        );
    }

    public long countUnreadByNim(String nim) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notifikasi WHERE nim = ? AND dibaca = 0",
                Long.class, nim
        );
        return count != null ? count : 0;
    }

    public Notifikasi insert(Notifikasi notifikasi) {
        String sql = "INSERT INTO notifikasi(nim, kode_dosen, pesan, tipe, dibaca, created_at) VALUES(?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, notifikasi.getNim());
            ps.setString(2, notifikasi.getKodeDosen());
            ps.setString(3, notifikasi.getPesan());
            ps.setString(4, notifikasi.getTipe());
            ps.setBoolean(5, notifikasi.isDibaca());
            ps.setTimestamp(6, Timestamp.valueOf(notifikasi.getCreatedAt()));
            return ps;
        }, keyHolder);
        
        notifikasi.setId(keyHolder.getKey().longValue());
        return notifikasi;
    }

    public boolean markAsRead(Long id) {
        return jdbcTemplate.update("UPDATE notifikasi SET dibaca = 1 WHERE id = ?", id) > 0;
    }

    public boolean markAllAsRead(String nim) {
        return jdbcTemplate.update("UPDATE notifikasi SET dibaca = 1 WHERE nim = ?", nim) > 0;
    }

    public List<Notifikasi> findByKodeDosen(String kodeDosen) {
        return jdbcTemplate.query(
                "SELECT * FROM notifikasi WHERE kode_dosen = ? ORDER BY created_at DESC LIMIT 20",
                mapper, kodeDosen
        );
    }

    public long countUnreadByKodeDosen(String kodeDosen) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notifikasi WHERE kode_dosen = ? AND dibaca = 0",
                Long.class, kodeDosen
        );
        return count != null ? count : 0;
    }

    public boolean markAllAsReadByKodeDosen(String kodeDosen) {
        return jdbcTemplate.update("UPDATE notifikasi SET dibaca = 1 WHERE kode_dosen = ?", kodeDosen) > 0;
    }
}

