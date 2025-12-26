package com.example.tubes_pbo.repository;

import com.example.tubes_pbo.model.Nilai;
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
public class NilaiRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Nilai> mapper = (rs, i) -> {
        Nilai n = new Nilai(
                rs.getString("mata_kuliah"),
                rs.getDouble("tugas"),
                rs.getDouble("uts"),
                rs.getDouble("uas"));
        n.setId(rs.getLong("id"));
        n.setNim(rs.getString("nim"));
        return n;
    };

    public NilaiRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Nilai> findByNim(String nim) {
        return jdbcTemplate.query(
                "select id, nim, mata_kuliah, tugas, uts, uas from nilai where nim = ? order by id",
                mapper,
                nim);
    }

    public Nilai insert(String nim, Nilai nilai) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "insert into nilai(nim, mata_kuliah, tugas, uts, uas) values(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nim);
            ps.setString(2, nilai.getMataKuliah());
            ps.setDouble(3, nilai.getTugas());
            ps.setDouble(4, nilai.getUts());
            ps.setDouble(5, nilai.getUas());
            return ps;
        }, kh);
        Number id = kh.getKey();
        if (id != null) {
            nilai.setId(id.longValue());
            nilai.setNim(nim);
        }
        return nilai;
    }

    public Optional<Nilai> findById(long id) {
        List<Nilai> list = jdbcTemplate.query(
                "select id, nim, mata_kuliah, tugas, uts, uas from nilai where id = ?",
                mapper,
                id);
        return list.stream().findFirst();
    }

    public boolean update(long id, Nilai nilai) {
        int rows = jdbcTemplate.update(
                "update nilai set mata_kuliah = ?, tugas = ?, uts = ?, uas = ? where id = ?",
                nilai.getMataKuliah(),
                nilai.getTugas(),
                nilai.getUts(),
                nilai.getUas(),
                id);
        return rows > 0;
    }

    public boolean deleteById(long id) {
        return jdbcTemplate.update("delete from nilai where id = ?", id) > 0;
    }

    public long count() {
        Long c = jdbcTemplate.queryForObject("select count(*) from nilai", Long.class);
        return c == null ? 0 : c;
    }

    public boolean existsByNimAndMataKuliah(String nim, String mataKuliah) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM nilai WHERE nim = ? AND mata_kuliah = ?",
                Long.class,
                nim, mataKuliah);
        return count != null && count > 0;
    }
}
