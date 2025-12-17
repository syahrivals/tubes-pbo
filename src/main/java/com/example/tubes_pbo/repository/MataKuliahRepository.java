package com.example.tubes_pbo.repository;

import com.example.tubes_pbo.model.MataKuliah;
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
public class MataKuliahRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<MataKuliah> mapper = (rs, i) -> {
        MataKuliah mk = new MataKuliah();
        mk.setId(rs.getLong("id"));
        mk.setKode(rs.getString("kode"));
        mk.setNama(rs.getString("nama"));
        mk.setSks(rs.getInt("sks"));
        mk.setKodeDosen(rs.getString("kode_dosen"));
        // namaDosen akan di-join jika ada
        try {
            mk.setNamaDosen(rs.getString("nama_dosen"));
        } catch (Exception e) {
            // ignore if column doesn't exist
        }
        return mk;
    };

    public MataKuliahRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MataKuliah> findAll() {
        String sql = "SELECT mk.*, d.nama as nama_dosen " +
                    "FROM mata_kuliah mk " +
                    "LEFT JOIN dosen d ON mk.kode_dosen = d.kode_dosen " +
                    "ORDER BY mk.kode";
        return jdbcTemplate.query(sql, mapper);
    }

    public List<MataKuliah> findByKodeDosen(String kodeDosen) {
        String sql = "SELECT mk.*, d.nama as nama_dosen " +
                    "FROM mata_kuliah mk " +
                    "LEFT JOIN dosen d ON mk.kode_dosen = d.kode_dosen " +
                    "WHERE mk.kode_dosen = ? " +
                    "ORDER BY mk.kode";
        return jdbcTemplate.query(sql, mapper, kodeDosen);
    }

    public Optional<MataKuliah> findById(Long id) {
        String sql = "SELECT mk.*, d.nama as nama_dosen " +
                    "FROM mata_kuliah mk " +
                    "LEFT JOIN dosen d ON mk.kode_dosen = d.kode_dosen " +
                    "WHERE mk.id = ?";
        List<MataKuliah> list = jdbcTemplate.query(sql, mapper, id);
        return list.stream().findFirst();
    }

    public Optional<MataKuliah> findByKode(String kode) {
        String sql = "SELECT mk.*, d.nama as nama_dosen " +
                    "FROM mata_kuliah mk " +
                    "LEFT JOIN dosen d ON mk.kode_dosen = d.kode_dosen " +
                    "WHERE mk.kode = ?";
        List<MataKuliah> list = jdbcTemplate.query(sql, mapper, kode);
        return list.stream().findFirst();
    }

    public MataKuliah insert(MataKuliah mataKuliah) {
        String sql = "INSERT INTO mata_kuliah(kode, nama, sks, kode_dosen) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, mataKuliah.getKode());
            ps.setString(2, mataKuliah.getNama());
            ps.setInt(3, mataKuliah.getSks());
            ps.setString(4, mataKuliah.getKodeDosen());
            return ps;
        }, keyHolder);
        
        mataKuliah.setId(keyHolder.getKey().longValue());
        return mataKuliah;
    }

    public boolean update(MataKuliah mataKuliah) {
        String sql = "UPDATE mata_kuliah SET kode = ?, nama = ?, sks = ?, kode_dosen = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, 
            mataKuliah.getKode(), 
            mataKuliah.getNama(), 
            mataKuliah.getSks(), 
            mataKuliah.getKodeDosen(),
            mataKuliah.getId()
        );
        return rows > 0;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM mata_kuliah WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    public long count() {
        Long c = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mata_kuliah", Long.class);
        return c == null ? 0 : c;
    }
}

