package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, new MpaMapper());
    }

    @Override
    public MpaRating getById(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new MpaMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static class MpaMapper implements RowMapper<MpaRating> {
        @Override
        public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MpaRating(rs.getInt("id"), rs.getString("mpa_name"));
        }
    }
}
