package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (id, title, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, (SELECT id FROM mpa_ratings WHERE name = ?))";

        Long filmId = film.getId() != 0 ? film.getId() : getNextFilmId();

        jdbcTemplate.update(sql, filmId, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpaRatings());

        film.setId(filmId);

        return film;
    }


    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id = (SELECT id FROM mpa_ratings WHERE name = ?) WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpaRatings(), film.getId());
        return getById(film.getId());
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getById(Long id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
    }

    public List<String> getAllMpaRatings() {
        String sql = "SELECT name FROM mpa_ratings";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    private Long getNextFilmId() {
        return jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) + 1 FROM films", Long.class);
    }

    private void saveGenres(Long filmId, Set<Genre> genres) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }


    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("title"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpaRatings(rs.getString("mpa_name"));
        return film;
    }
}