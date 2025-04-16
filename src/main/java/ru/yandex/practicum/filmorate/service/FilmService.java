package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       GenreStorage genreStorage, MpaStorage mpaStorage, DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.directorStorage = directorStorage;
    }

    public Film create(Film film) {
        validateFilm(film);
        checkDirectorExistence(film);
        validateGenreAndMpaExistence(film);
        log.info("Создание фильма: {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        getByIdForVal(film.getId());
        validateGenreAndMpaExistence(film);
        log.info("Обновление фильма: {}", film);
        return filmStorage.update(film);
    }

    private void validateGenreAndMpaExistence(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {

                if (genreStorage.getGenreById(genre.getId()) == null) {
                    throw new GenreNotFoundException("Genre with id " + genre.getId() + " not found.");
                }
            }
        }
        if (mpaStorage.getById(film.getMpa().getId()) == null) {
            throw new MpaRatingNotFoundException("Mpa rating with id " + film.getMpa().getId() + " not found.");
        }
    }

    public Collection<Film> getAll() {
        log.info("Запрос всех фильмов");
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        log.info("Запрос фильма с id={}", id);
        Film film = filmStorage.getById(id);
        if (film == null) {
            log.warn("Фильм с id {} не найден", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Попытка добавить лайк: filmId={}, userId={}", filmId, userId);

        filmStorage.getById(filmId);
        userStorage.getById(userId);

        filmStorage.addLike(filmId, userId);
        log.info("Лайк успешно добавлен: filmId={}, userId={}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Попытка удалить лайк: filmId={}, userId={}", filmId, userId);

        getById(filmId);
        User user = userStorage.getById(userId);

        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        filmStorage.removeLike(filmId, userId);
        log.info("Лайк удалён: filmId={}, userId={}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        if (count <= 0) {
            throw new ValidationException("Количество фильмов должно быть больше 0");
        }
        if (genreId != null && genreStorage.getGenreById(genreId) == null) {
            throw new GenreNotFoundException("Жанр с id " + genreId + " не найден");
        }

        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public void getByIdForVal(Long id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            log.warn("Фильм с id {} не найден", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
    }

    public void removeFilm(Long id) {
        if (!filmStorage.removeFilm(id)) {
            log.error("Ошибка удаления фильма id {}", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
        log.info("Фильм с id {} удалён", id);
    }

    public Collection<Film> searchFilms(String query, Set<String> by) {
        if (by.isEmpty() || by.size() > 2 || !Set.of("title", "director").containsAll(by)) {
            throw new ValidationException("Параметр by может принимать значение title, director или оба");
        }
        if (query == null || query.isEmpty()) {
            return getAll();
        }
        log.info("Запрос на поиск фильма: query={}, by={}", query, by);
        return filmStorage.searchFilmsByQuery(query, by);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не может быть пустой");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        checkDirectorIdOrThrow(directorId);
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    private void checkDirectorIdOrThrow(Long id) {
        directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Директор с id " + id + " не найден"));
    }

    private void checkDirectorExistence(Film film) {
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                directorStorage.getDirectorById(director.getId())
                        .orElseThrow(() -> new NotFoundException("Режиссер с id " + director.getId() + " не найден"));
            }
        }
    }
}

