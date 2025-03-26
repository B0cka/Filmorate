package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    @PostMapping
    public Film create(@RequestBody Film film);

    @PutMapping
    public Film update(@RequestBody Film newFilm);

    @GetMapping
    Collection<Film> getAll();

    Film getById(Long id);
}
