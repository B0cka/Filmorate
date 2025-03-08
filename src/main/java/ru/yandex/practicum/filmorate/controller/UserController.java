package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);

        if (!users.containsKey(newUser.getId())) {
            log.warn("Ошибка: Пользователь с id={} не найден", newUser.getId());
            throw new ValidationException("Пользователь с id=" + newUser.getId() + " не найден");
        }

        validateUser(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь обновлён: {}", newUser);
        return newUser;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Запрос на получение всех пользователей");
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

    private long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
