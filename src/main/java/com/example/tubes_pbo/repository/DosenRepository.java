package com.example.tubes_pbo.repository;

import com.example.tubes_pbo.model.Dosen;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DosenRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Dosen> mapper = (rs, i) -> new Dosen(
            rs.getString("kode_dosen"),
            rs.getString("nama"),
            rs.getString("username"),
            rs.getString("password")
    );

    public DosenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Dosen> findByUsername(String username) {
        List<Dosen> list = jdbcTemplate.query(
                "select kode_dosen, nama, username, password from dosen where username = ?",
                mapper,
                username
        );
        return list.stream().findFirst();
    }
}

