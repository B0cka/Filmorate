package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    @PostMapping
    User create(@RequestBody User user);

    @PutMapping
    User update(@RequestBody User newUser);

    @GetMapping
    Collection<User> getAll();

    User getById(Long id);
}
