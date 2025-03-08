package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;

class FilmControllerTest {
    private FilmController filmController;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        validFilm = new Film();
        validFilm.setName("Inception");
        validFilm.setDescription("A mind-bending thriller.");
        validFilm.setReleaseDate(LocalDate.parse("2010-07-16"));
        validFilm.setDuration(148);
    }

    @Test
    void shouldNotCreateFilmWithTooLongDescription() {
        validFilm.setDescription("A".repeat(201));
        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(validFilm);
        });
        Assertions.assertEquals("Максимальная длина описания — 200 символов", thrown.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithReleaseDateBefore1895() {
        validFilm.setReleaseDate(LocalDate.parse("1890-01-01"));
        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(validFilm);
        });
        Assertions.assertEquals("Дата релиза — не раньше 28 декабря 1895 года", thrown.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithNegativeDuration() {
        validFilm.setDuration(-10010);
        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(validFilm);
        });
        Assertions.assertEquals("Продолжительность фильма должна быть положительным числом", thrown.getMessage());
    }
}
