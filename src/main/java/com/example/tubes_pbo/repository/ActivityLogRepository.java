package com.example.tubes_pbo.repository;

import com.example.tubes_pbo.model.ActivityLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ActivityLogRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ActivityLog> mapper = (rs, i) -> {
        ActivityLog log = new ActivityLog();
        log.setId(rs.getLong("id"));
        log.setUserRole(rs.getString("user_role"));
        log.setUsername(rs.getString("username"));
        log.setAction(rs.getString("action"));
        log.setEntityType(rs.getString("entity_type"));
        log.setEntityId(rs.getString("entity_id"));
        log.setDescription(rs.getString("description"));
        Timestamp ts = rs.getTimestamp("timestamp");
        if (ts != null) {
            log.setTimestamp(ts.toLocalDateTime());
        }
        return log;
    };

    public ActivityLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ActivityLog> findAll(int limit) {
        return jdbcTemplate.query(
                "SELECT * FROM activity_log ORDER BY timestamp DESC LIMIT ?",
                mapper, limit
        );
    }

    public List<ActivityLog> findByUserRole(String userRole, int limit) {
        return jdbcTemplate.query(
                "SELECT * FROM activity_log WHERE user_role = ? ORDER BY timestamp DESC LIMIT ?",
                mapper, userRole, limit
        );
    }

    public List<ActivityLog> findByEntityType(String entityType, int limit) {
        return jdbcTemplate.query(
                "SELECT * FROM activity_log WHERE entity_type = ? ORDER BY timestamp DESC LIMIT ?",
                mapper, entityType, limit
        );
    }

    public ActivityLog insert(ActivityLog log) {
        String sql = "INSERT INTO activity_log(user_role, username, action, entity_type, entity_id, description, timestamp) VALUES(?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, log.getUserRole());
            ps.setString(2, log.getUsername());
            ps.setString(3, log.getAction());
            ps.setString(4, log.getEntityType());
            ps.setString(5, log.getEntityId());
            ps.setString(6, log.getDescription());
            ps.setTimestamp(7, Timestamp.valueOf(log.getTimestamp()));
            return ps;
        }, keyHolder);
        
        log.setId(keyHolder.getKey().longValue());
        return log;
    }
}

