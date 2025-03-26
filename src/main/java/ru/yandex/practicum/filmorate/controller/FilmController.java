package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

@RestController
@RequestMapping("/films")
public class FilmController {

    final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return inMemoryFilmStorage.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return inMemoryFilmStorage.update(newFilm);
    }

    @GetMapping
    public Collection<Film> getAll() {
        return inMemoryFilmStorage.getAll();
    }

}
