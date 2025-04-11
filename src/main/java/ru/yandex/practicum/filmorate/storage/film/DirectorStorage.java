package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

public interface DirectorStorage {

    Set<String> getAllDirectors();

    String getDirectorById(long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long directorId);
}
