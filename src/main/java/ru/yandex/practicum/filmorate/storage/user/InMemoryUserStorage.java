package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long currentMaxId = 0;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    @Override
    public User create(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        validateUser(user);


        if (users.containsKey(user.getId())) {
            log.warn("Ошибка: Пользователь с id={} уже существует", user.getId());
            throw new ValidationException("Пользователь с таким ID уже существует");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @PutMapping
    @Override
    public User update(@RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);
        if (users.isEmpty()) {
            throw new ValidationException("Пока что нет юзеров");
        }

        if (newUser.getId() == 0 || !users.containsKey(newUser.getId())) {
            log.warn("Ошибка: Пользователь с id={} не найден", newUser.getId());
            throw new UserNotFoundException("Пользователь с id=" + newUser.getId() + " не найден");
        }

        validateUser(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь обновлён: {}", newUser);
        return newUser;
    }

    @GetMapping
    @Override
    public Collection<User> getAll() {
        log.info("Запрос на получение всех пользователей");
        if (users.isEmpty()) {
            throw new ValidationException("Пока что нет юзеров");
        }

        return users.values();
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка: Логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка: Логин не должен содержать пробелы");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Ошибка: Имейл должен быть указан");
            throw new ValidationException("Имейл должен быть указан");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Ошибка: В имейле должна быть @");
            throw new ValidationException("В имейле должна быть @");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка: Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Ошибка: Пользователь с id={} не найден", id);
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }
        return users.get(id);
    }

    private long getNextId() {
        currentMaxId++;
        return currentMaxId;
    }
}
