package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    JdbcTemplate jdbcTemplate;

    @Override
    public Set<String> getAllDirectors() {
        String sql = "SELECT name FROM directors";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name")));
    }


    @Override
    public String getDirectorById(long id) {
        String sql = "SELECT name FROM directors WHERE director_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getString("name"), id);
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET" +
                " name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql,
                director.getId(),
                director.getName());
        return director;
    }

    @Override
    public void deleteDirector(long directorId) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, directorId);
    }
}
