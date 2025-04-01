package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public Film create(Film film) {
        validateFilm(film);
        log.info("Создание фильма: {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        log.info("Обновление фильма: {}", film);
        return filmStorage.update(film);
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

        Film film = getById(filmId);
        User user = userStorage.getById(userId);

        if (user == null) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь {} уже поставил лайк фильму {}", userId, filmId);
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        film.getLikes().add(userId);
        log.info("Лайк успешно добавлен: filmId={}, userId={}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Попытка удалить лайк: filmId={}, userId={}", filmId, userId);
        Film film = getById(filmId);

        if (!film.getLikes().contains(userId)) {
            throw new UserNotFoundException("Лайк от пользователя с id " + userId + " не найден");
        }

        film.getLikes().remove(userId);
        log.info("Лайк удален: filmId={}, userId={}", filmId, userId);
    }

    public List<Film> getPopularFilms() {
        log.info("Запрос популярных фильмов");
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(10)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}

