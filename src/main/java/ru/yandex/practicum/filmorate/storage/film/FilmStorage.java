package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film newFilm);

    Collection<Film> getAll();

    Film getById(Long id);

    void removeFilms(Long id);

}
