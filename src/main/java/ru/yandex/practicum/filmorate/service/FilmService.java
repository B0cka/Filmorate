package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;

    }

    public Film create(Film film) {
        validateFilm(film);
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

        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);

        filmStorage.addLike(filmId, userId);
        log.info("Лайк успешно добавлен: filmId={}, userId={}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Попытка удалить лайк: filmId={}, userId={}", filmId, userId);

        Film film = getById(filmId);

        filmStorage.removeLike(filmId, userId);
        log.info("Лайк удалён: filmId={}, userId={}", filmId, userId);
    }


    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
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

    public void deleteFilm(Long id) {
        if (!filmStorage.deleteFilm(id)) {
            log.error("Ошибка удаления фильма id {}", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
        log.info("Фильм с id {} удалён", id);
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

}

