package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.model.FeedEventType.LIKE;
import static ru.yandex.practicum.filmorate.model.FeedOperationType.ADD;
import static ru.yandex.practicum.filmorate.model.FeedOperationType.REMOVE;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorDbStorage directorDbStorage;
    private final FeedDbStorage feedStorage;

    @Override
    public Film create(Film film) {
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                if (directorDbStorage.getDirectorById(director.getId()).isEmpty()) {
                    throw new NotFoundException("Режиссер с id " + director.getId() + " не найден");
                }
            }
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

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

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genresSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            Set<Integer> seenGenres = new HashSet<>();
            for (Genre genre : film.getGenres()) {
                if (seenGenres.add(genre.getId())) {
                    jdbcTemplate.update(genresSql, film.getId(), genre.getId());
                }
            }
        }
        updateDirectors(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        updateDirectors(film);
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);

        return getById(film.getId());
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.id as mpa_id, m.mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper());
        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setLikes(getLikesByFilmId(film.getId()));
            film.setDirectors(getDirectorsByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public Film getById(Long id) {
        String sql = """
                SELECT f.*, m.id AS mpa_id, m.mpa_name
                FROM films f
                LEFT JOIN mpa_ratings m ON f.mpa_id = m.id
                WHERE f.id = ?
                """;

        try {
            Film film = jdbcTemplate.queryForObject(sql, new FilmMapper(), id);
            if (film != null) {
                film.setGenres(getGenresByFilmId(id));
                film.setLikes(getLikesByFilmId(id));
                film.setDirectors(getDirectorsByFilmId(id));
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql = """
                SELECT f.*, m.mpa_name AS mpa_name, m.id AS mpa_id
                FROM films AS f
                JOIN film_likes AS fl ON fl.film_id = f.id
                JOIN mpa_ratings m ON f.mpa_id = m.id
                LEFT JOIN film_genres fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id
                WHERE f.id IN (
                    SELECT fl1.film_id
                    FROM film_likes fl1
                    JOIN film_likes fl2 ON fl1.film_id = fl2.film_id
                    WHERE fl1.user_id = ? AND fl2.user_id = ?
                )
                GROUP BY f.id, m.mpa_name, m.id
                ORDER BY COUNT(fl.user_id) DESC;
                """;
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper(), userId, friendId);
        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setLikes(getLikesByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
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
            film.setDirectors(getDirectorsByFilmId(film.getId()));
        }

        return films;
    }

    @Override
    public Collection<Film> searchFilmsByQuery(String query, Set<String> by) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(fl.user_id) AS like_count, " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.id " +
                "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.director_id "
        );

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String search = "%" + query.toLowerCase() + "%";

        if (by.contains("title")) {
            conditions.add("LOWER(f.name) LIKE ?");
            params.add(search);
        }

        if (by.contains("director")) {
            conditions.add("LOWER(d.name) LIKE ?");
            params.add(search);
        }

        sqlBuilder.append(" WHERE ").append(String.join(" OR ", conditions));
        sqlBuilder.append(" GROUP BY f.id, m.mpa_name ORDER BY like_count DESC");

        List<Film> films = jdbcTemplate.query(
                sqlBuilder.toString(),
                new FilmMapper(),
                params.toArray()
        );

        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setLikes(getLikesByFilmId(film.getId()));
            film.setDirectors(getDirectorsByFilmId(film.getId()));
        }

        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, filmId, userId);
        feedStorage.save(LIKE, ADD, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        if (jdbcTemplate.update(sql, filmId, userId) > 0) {
            feedStorage.save(LIKE, REMOVE, filmId, userId);
        }
    }

    @Override
    public boolean removeFilm(Long id) {
        String sql = "DELETE FROM films WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        String sql = """
                SELECT f.*, m.id AS mpa_id, m.mpa_name
                FROM films f
                INNER JOIN film_directors fd ON f.id = fd.film_id
                LEFT JOIN mpa_ratings m ON f.mpa_id = m.id
                WHERE fd.director_id = ?""";

        if ("year".equals(sortBy)) {
            sql += " ORDER BY f.release_date";
        } else {
            sql += " ORDER BY f.id ASC";
        }

        List<Film> films = jdbcTemplate.query(sql, new FilmMapper(), directorId);

        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setLikes(getLikesByFilmId(film.getId()));
            film.setDirectors(getDirectorsByFilmId(film.getId()));
        }
        return films;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            Set<Integer> seen = new HashSet<>();
            for (Genre genre : film.getGenres()) {
                if (seen.add(genre.getId())) {
                    jdbcTemplate.update(sql, film.getId(), genre.getId());
                }
            }
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private LinkedHashSet<Genre> getGenresByFilmId(Long filmId) {
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
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, filmId));
    }

    private Set<Director> getDirectorsByFilmId(Long filmId) {
        String sql = "SELECT d.director_id, d.name FROM directors d " +
                "JOIN film_directors fd ON d.director_id = fd.director_id " +
                "WHERE fd.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql,
                (rs, rowNum) -> new Director(rs.getString("name"), rs.getLong("director_id")),
                filmId));
    }

    private void updateDirectors(Film film) {
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
            Set<Long> uniqueDirectorIds = new HashSet<>();

            for (Director director : film.getDirectors()) {
                if (director != null) {
                    boolean exists = Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                            "SELECT EXISTS(SELECT 1 FROM directors WHERE director_id = ?)",
                            Boolean.class,
                            director.getId()
                    ));
                    if (!exists) {
                        throw new NotFoundException("Режиссер с id " + director.getId() + " не найден");
                    }

                    if (uniqueDirectorIds.add(director.getId())) {
                        jdbcTemplate.update(sql, film.getId(), director.getId());
                    }
                }
            }
        }
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        String sql = """
                SELECT f.*, m.id, m.mpa_name
                FROM films AS f
                LEFT JOIN mpa_ratings m ON f.mpa_id = m.id
                INNER JOIN (SELECT film_id FROM film_likes f_l
                INNER JOIN (SELECT user_id2.user_id
                FROM (SELECT film_id FROM film_likes WHERE user_id = ?) user_id1
                LEFT JOIN film_likes user_id2
                ON user_id1.film_id = user_id2.film_id AND user_id2.user_id <> ?
                GROUP BY user_id2.user_id
                ORDER BY COUNT(user_id2.film_id) DESC
                LIMIT 1) curr_user
                ON f_l.user_id = curr_user.user_id
                EXCEPT
                SELECT film_id FROM film_likes WHERE user_id = ?) ff
                ON f.id = ff.film_id
                """;

        List<Film> films = jdbcTemplate.query(sql, new FilmMapper(), id, id, id);
        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setLikes(getLikesByFilmId(film.getId()));
            film.setDirectors(getDirectorsByFilmId(film.getId()));
        }
        return films;
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
                    new HashSet<>(),
                    new LinkedHashSet<>(),
                    new MpaRating(rs.getInt("mpa_id"), rs.getString("mpa_name")),
                    new HashSet<>()
            );
        }
    }
}
