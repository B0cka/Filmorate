package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private LocalDate releaseDate = LocalDate.parse("1895-12-28");

    @PostMapping
    public Film create(@RequestBody Film film) {

        log.info("Получен запрос на создание фильма: {}", film);

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка: название не может быть пустым");
            throw new ValidationException("название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.warn("Ошибка: максимальная длина описания — 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate().isBefore(releaseDate)) {
            log.warn("Ошибка: Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 0) {
            log.warn("Ошибка: продолжительность фильма должна быть положительным числом");
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм записан: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма: {}", newFilm);

        if (newFilm.getId() == 0) {
            log.warn("Ошибка: Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Ошибка: Фильм с id={} не найден", newFilm.getId());
            throw new ValidationException("Фильм с id=" + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            log.warn("Ошибка: Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (newFilm.getReleaseDate().isBefore(releaseDate)) {
            log.warn("Ошибка: Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        films.put(newFilm.getId(), newFilm);
        log.info("Фильм обновлён: {}", newFilm);

        return newFilm;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Запрос на получение всех фильмов");
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
