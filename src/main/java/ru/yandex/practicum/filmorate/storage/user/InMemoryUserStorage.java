package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;@Component

public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long currentMaxId = 0;

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new UserNotFoundException("Пользователь с id=" + newUser.getId() + " не найден");
        }
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }
        return users.get(id);
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}
