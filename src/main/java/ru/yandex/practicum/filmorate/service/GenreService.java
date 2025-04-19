package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        Genre genre = genreStorage.getGenreById(id);
        if (genre == null) {
            throw new GenreNotFoundException("Жанр с id " + id + " не найден");
        }
        return genre;
    }
}
