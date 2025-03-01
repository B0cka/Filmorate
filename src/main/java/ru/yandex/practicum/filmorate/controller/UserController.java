package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);

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
        if (user.getBirthday() == null || LocalDate.now().isBefore(user.getBirthday())) {
            log.warn("Ошибка : Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            user.setUsername(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь создан: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);

        if (newUser.getId() == 0) {
            log.warn("Ошибка: Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (!users.containsKey(newUser.getId())) {
            log.warn("Ошибка: Пользователь с id={} не найден", newUser.getId());
            throw new ValidationException("Пользователь с id=" + newUser.getId() + " не найден");
        }
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            log.warn("Ошибка: Имейл должен быть указан");
            throw new ValidationException("Имейл должен быть указан");
        }
        if (!newUser.getEmail().contains("@")) {
            log.warn("Ошибка: В имейле должна быть @");
            throw new ValidationException("В имейле должна быть @");
        }
        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.warn("Ошибка: Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (newUser.getBirthday() == null || newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка: Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (newUser.getUsername() == null || newUser.getUsername().isBlank()) {
            newUser.setUsername(newUser.getLogin());
        }

        users.put(newUser.getId(), newUser);
        log.info("Пользователь обновлён: {}", newUser);

        return newUser;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Запрос на получение всех пользователей");
        return users.values();
    }

    private long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
