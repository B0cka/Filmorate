package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;

import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;

public class FilmorateApplicationTests {

    private UserController userController;
    private FilmController filmController;
    private User validUser;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        // Инициализация контроллеров и объектов
        userController = new UserController();
        filmController = new FilmController();

        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testUser");
        validUser.setUsername("Test");
        validUser.setBirthday(LocalDate.parse("1990-01-01"));

        validFilm = new Film();
        validFilm.setName("Inception");
        validFilm.setDescription("A mind-bending thriller.");
        validFilm.setReleaseDate(LocalDate.parse("2010-07-16"));
        validFilm.setDuration(148);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        User createdUser = userController.create(validUser);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals("test@example.com", createdUser.getEmail());
        Assertions.assertEquals("testUser", createdUser.getLogin());
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        validUser.setEmail("invalid-email");

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(validUser);
        });

        Assertions.assertEquals("В имейле должна быть @", thrown.getMessage());
    }

    @Test
    void shouldCreateFilmSuccessfully() {
        Film createdFilm = filmController.create(validFilm);

        Assertions.assertNotNull(createdFilm);
        Assertions.assertEquals("Inception", createdFilm.getName());
        Assertions.assertEquals(148, createdFilm.getDuration());
    }

    @Test
    void shouldNotCreateFilmWithTooLongDescription() {
        validFilm.setDescription("A".repeat(201));

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(validFilm);
        });

        Assertions.assertEquals("максимальная длина описания — 200 символов", thrown.getMessage());
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
        validFilm.setDuration(-120);

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(validFilm);
        });

        Assertions.assertEquals("продолжительность фильма должна быть положительным числом", thrown.getMessage());
    }
}
