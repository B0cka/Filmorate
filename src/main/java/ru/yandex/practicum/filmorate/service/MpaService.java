package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MpaRating> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public MpaRating getById(int id) {
        MpaRating rating = mpaStorage.getById(id);
        if (rating == null) {
            throw new MpaRatingNotFoundException("MPA с id " + id + " не найден");
        }
        return rating;
    }
}
