package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;


public interface FilmStorage {

    Film create(Film film);

    Film update(Film newFilm);

    Collection<Film> getAll();

    Film getById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    public List<Film> getPopularFilms(int count, Long genreId, Integer year);

    boolean removeFilm(Long id);

}
