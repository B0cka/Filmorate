package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MpaRatingNotFoundException extends RuntimeException {
    public MpaRatingNotFoundException(String message) {
        super(message);
    }

    public MpaRatingNotFoundException(Integer id) {
        super("MPA с id " + id + " не найден");
    }
}