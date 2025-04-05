package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(@Qualifier("userDbStorage")UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(Long id) {
        log.info("Запрос пользователя с id={}", id);
        User user = userStorage.getById(id);
        if (user == null) {
            log.warn("Пользователь с id={} не найден", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        log.info("Пользователь найден: {}", user);
        return user;
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        return user.getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public void addFriend(Long id, Long friendId) {
        log.info("Попытка добавить в друзья: userId={}, friendId={}", id, friendId);

        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);

        if (user == null || friend == null) {
            log.warn("Один из пользователей не найден: userId={}, friendId={}", id, friendId);
            throw new UserNotFoundException("Один из пользователей не найден");
        }

        if (id.equals(friendId)) {
            log.warn("Пользователь {} пытается добавить себя в друзья", id);
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.info("Пользователи {} и {} теперь друзья", id, friendId);
    }


    public void removeFriend(Long id, Long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (friend == null) {
            throw new NotFoundException("Friend not found");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }


    public List<User> getCommonFriends(Long id, Long otherId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(otherId);

        if (user1 == null || user2 == null) {
            throw new UserNotFoundException("Один из пользователей не найден");
        }

        Set<Long> commonFriends = user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .collect(Collectors.toSet());

        return commonFriends.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Имейл должен быть указан");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("В имейле должна быть @");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
    public void getByIdForVal(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            log.warn("User с id {} не найден", id);
            throw new FilmNotFoundException("User с id " + id + " не найден");
        }

    }
    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        getByIdForVal(user.getId());
        validateUser(user);
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }
}
