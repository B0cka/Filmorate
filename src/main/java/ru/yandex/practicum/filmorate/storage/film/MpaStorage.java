package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaStorage {

    List<MpaRating> getAllMpa();

    public MpaRating getById(int id);
}
