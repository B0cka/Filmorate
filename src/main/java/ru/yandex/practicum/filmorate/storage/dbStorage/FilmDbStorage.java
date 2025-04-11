package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        saveGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);

        return film;
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.id as mpa_id, m.mpa_name FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper());
        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setLikes(getLikesByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public Film getById(Long id) {
        String sql = "SELECT f.*, m.id as mpa_id, m.mpa_name FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, new FilmMapper(), id);
            film.setGenres(getGenresByFilmId(id));
            film.setLikes(getLikesByFilmId(id));
            return film;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name, COUNT(fl.user_id) AS like_count " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.id " +
                "WHERE (? IS NULL OR g.id = ?) " +
                "AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?) " +
                "GROUP BY f.id, m.mpa_name " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";


        List<Film> films = jdbcTemplate.query(
                sql,
                new FilmMapper(),
                genreId, genreId, year, year, count
        );


        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setLikes(getLikesByFilmId(film.getId()));
        }

        return films;
    }


    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public boolean deleteFilm(Long id) {
        String sql = "DELETE FROM films WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            Set<Long> seen = new HashSet<>();
            for (Genre genre : film.getGenres()) {
                if (seen.add(genre.getId())) {
                    jdbcTemplate.update(sql, film.getId(), genre.getId());
                }
            }
        }
    }

    private Set<Genre> getGenresByFilmId(Long filmId) {
        String sql = """
                    SELECT g.id, g.name
                    FROM genres g
                    JOIN film_genres fg ON g.id = fg.genre_id
                    WHERE fg.film_id = ?
                """;
        return new LinkedHashSet<>(jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                filmId));
    }

    private Set<Long> getLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    public class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Film(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    new MpaRating(rs.getInt("mpa_id"), rs.getString("mpa_name")),
                    new HashSet<>()
            );
        }
    }
}