package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User create(@RequestBody User user);

    User update(@RequestBody User newUser);

    Collection<User> getAll();

    User getById(Long id);
}
