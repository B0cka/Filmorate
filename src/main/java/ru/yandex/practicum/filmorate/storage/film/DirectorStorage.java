package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {

    Set<Director> getAllDirectors();

    Optional<Director> getDirectorById(Long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long directorId);
}
