package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final LocalDate releaseDate = LocalDate.parse("1895-12-28");

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм записан: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма: {}", newFilm);
        if (films.isEmpty()) {
            throw new ValidationException("Пока что нет фильмов");
        }
        if (newFilm.getId() == 0 || !films.containsKey(newFilm.getId())) {
            log.warn("Ошибка: фильм с id={} не найден", newFilm.getId());
            throw new FilmNotFoundException("Фильм с id=" + newFilm.getId() + " не найден");
        }

        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм обновлён: {}", newFilm);

        return newFilm;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Запрос на получение всех фильмов");
        return films.values();
    }


    @Override
    public Film getById(Long id) {

        return films.get(id);
    }

    private long getNextId() {
        return films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка: Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Ошибка: Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(releaseDate)) {
            log.warn("Ошибка: Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка: Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
