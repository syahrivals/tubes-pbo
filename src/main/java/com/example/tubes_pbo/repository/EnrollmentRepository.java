package com.example.tubes_pbo.repository;

import com.example.tubes_pbo.model.Enrollment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class EnrollmentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Enrollment> mapper = (rs, i) -> {
        Enrollment e = new Enrollment();
        e.setId(rs.getLong("id"));
        e.setNim(rs.getString("nim"));
        e.setMataKuliahId(rs.getLong("mata_kuliah_id"));
        e.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("enrolled_at");
        if (ts != null) {
            e.setEnrolledAt(ts.toLocalDateTime());
        }
        
        // Joined fields (optional)
        try {
            e.setNamaMahasiswa(rs.getString("nama_mahasiswa"));
            e.setKodeMataKuliah(rs.getString("kode_mata_kuliah"));
            e.setNamaMataKuliah(rs.getString("nama_mata_kuliah"));
            e.setSks(rs.getInt("sks"));
        } catch (Exception ex) {
            // ignore if columns don't exist
        }
        return e;
    };

    public EnrollmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Enrollment> findByNim(String nim) {
        String sql = "SELECT e.*, m.nama as nama_mahasiswa, " +
                    "mk.kode as kode_mata_kuliah, mk.nama as nama_mata_kuliah, mk.sks " +
                    "FROM enrollment e " +
                    "LEFT JOIN mahasiswa m ON e.nim = m.nim " +
                    "LEFT JOIN mata_kuliah mk ON e.mata_kuliah_id = mk.id " +
                    "WHERE e.nim = ? " +
                    "ORDER BY e.enrolled_at DESC";
        return jdbcTemplate.query(sql, mapper, nim);
    }

    public List<Enrollment> findByMataKuliahId(Long mataKuliahId) {
        String sql = "SELECT e.*, m.nama as nama_mahasiswa, " +
                    "mk.kode as kode_mata_kuliah, mk.nama as nama_mata_kuliah, mk.sks " +
                    "FROM enrollment e " +
                    "LEFT JOIN mahasiswa m ON e.nim = m.nim " +
                    "LEFT JOIN mata_kuliah mk ON e.mata_kuliah_id = mk.id " +
                    "WHERE e.mata_kuliah_id = ? " +
                    "ORDER BY e.enrolled_at DESC";
        return jdbcTemplate.query(sql, mapper, mataKuliahId);
    }

    public Optional<Enrollment> findById(Long id) {
        String sql = "SELECT e.*, m.nama as nama_mahasiswa, " +
                    "mk.kode as kode_mata_kuliah, mk.nama as nama_mata_kuliah, mk.sks " +
                    "FROM enrollment e " +
                    "LEFT JOIN mahasiswa m ON e.nim = m.nim " +
                    "LEFT JOIN mata_kuliah mk ON e.mata_kuliah_id = mk.id " +
                    "WHERE e.id = ?";
        List<Enrollment> list = jdbcTemplate.query(sql, mapper, id);
        return list.stream().findFirst();
    }

    public Optional<Enrollment> findByNimAndMataKuliahId(String nim, Long mataKuliahId) {
        String sql = "SELECT * FROM enrollment WHERE nim = ? AND mata_kuliah_id = ?";
        List<Enrollment> list = jdbcTemplate.query(sql, mapper, nim, mataKuliahId);
        return list.stream().findFirst();
    }

    public Enrollment insert(Enrollment enrollment) {
        String sql = "INSERT INTO enrollment(nim, mata_kuliah_id, status, enrolled_at) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, enrollment.getNim());
            ps.setLong(2, enrollment.getMataKuliahId());
            ps.setString(3, enrollment.getStatus());
            ps.setTimestamp(4, Timestamp.valueOf(enrollment.getEnrolledAt()));
            return ps;
        }, keyHolder);
        
        enrollment.setId(keyHolder.getKey().longValue());
        return enrollment;
    }

    public boolean updateStatus(Long id, String status) {
        String sql = "UPDATE enrollment SET status = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, status, id);
        return rows > 0;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM enrollment WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    public long count() {
        Long c = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM enrollment", Long.class);
        return c == null ? 0 : c;
    }

    public long countByNim(String nim) {
        Long c = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM enrollment WHERE nim = ?", Long.class, nim);
        return c == null ? 0 : c;
    }

    public List<Enrollment> findPendingByKodeDosen(String kodeDosen) {
        String sql = "SELECT e.*, m.nama as nama_mahasiswa, " +
                    "mk.kode as kode_mata_kuliah, mk.nama as nama_mata_kuliah, mk.sks " +
                    "FROM enrollment e " +
                    "LEFT JOIN mahasiswa m ON e.nim = m.nim " +
                    "LEFT JOIN mata_kuliah mk ON e.mata_kuliah_id = mk.id " +
                    "WHERE mk.kode_dosen = ? AND e.status = 'PENDING' " +
                    "ORDER BY e.enrolled_at DESC";
        return jdbcTemplate.query(sql, mapper, kodeDosen);
    }

    public List<Enrollment> findByKodeDosen(String kodeDosen) {
        String sql = "SELECT e.*, m.nama as nama_mahasiswa, " +
                    "mk.kode as kode_mata_kuliah, mk.nama as nama_mata_kuliah, mk.sks " +
                    "FROM enrollment e " +
                    "LEFT JOIN mahasiswa m ON e.nim = m.nim " +
                    "LEFT JOIN mata_kuliah mk ON e.mata_kuliah_id = mk.id " +
                    "WHERE mk.kode_dosen = ? " +
                    "ORDER BY e.enrolled_at DESC";
        return jdbcTemplate.query(sql, mapper, kodeDosen);
    }

    public int deleteByNim(String nim) {
        String sql = "DELETE FROM enrollment WHERE nim = ?";
        return jdbcTemplate.update(sql, nim);
    }
}
