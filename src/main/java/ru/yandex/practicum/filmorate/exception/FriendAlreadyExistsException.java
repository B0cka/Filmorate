package ru.yandex.practicum.filmorate.exception;

public class FriendAlreadyExistsException extends RuntimeException {
    public FriendAlreadyExistsException(String message) {
        super(message);
    }
}
