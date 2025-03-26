package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = filmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addLike(Long filmId, Long userId) {
        // Получаем фильм по ID
        Film film = inMemoryFilmStorage.getById(filmId);

        if (film == null) {
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден");
        }

        User user = inMemoryUserStorage.getById(userId); // Получаем пользователя
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        // Добавляем лайк
        film.getLikes().add(userId);
    }


    public void removeLike(Long filmId, Long userId) {
        Film film = inMemoryFilmStorage.getById(filmId);

        if (film == null) {
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден");
        }

        if (!film.getLikes().contains(userId)) {
            throw new FilmNotFoundException("Лайк от пользователя с id " + userId + " не найден");
        }

        film.getLikes().remove(userId);
    }



    public List<Film> getPopularFilms(int count) {
        return inMemoryFilmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size())) // Сортируем по количеству лайков
                .limit(count) // Берём top-N фильмов
                .collect(Collectors.toList());
    }

}

