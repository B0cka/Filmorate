package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public interface FilmStorage {

    Film create(Film film);

    Film update(Film newFilm);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

    Collection<Film> getAll();

    Film getById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    boolean removeFilm(Long id);

    boolean existsById(Long id);

    List<Film> getRecommendations(Long id);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getPopularFilms(int count, Integer genreId, Integer year);

    Collection<Film> searchFilmsByQuery(String query, Set<String> by);

}
