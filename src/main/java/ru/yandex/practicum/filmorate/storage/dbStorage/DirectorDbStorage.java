package ru.yandex.practicum.filmorate.storage.dbStorage;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<Director> getAllDirectors() {
        String sql = "SELECT director_id, name FROM directors";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                new Director(rs.getString("name"), rs.getLong("director_id"))));
    }

    @Override
    public Optional<Director> getDirectorById(Long id) {
        String sql = "SELECT director_id, name FROM directors WHERE director_id = ?";
        try {
            Director director = jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Director(
                            rs.getString("name"),
                            rs.getLong("director_id")
                    ),
                    id
            );
            return Optional.ofNullable(director);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

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
    @Transactional
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        int updated = jdbcTemplate.update(sql, director.getName(), director.getId());

        if (updated == 0) {
            throw new NotFoundException("Режиссер не найден");
        }
        return director;
    }

    @Override
    public void deleteDirector(long directorId) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, directorId);
    }
}