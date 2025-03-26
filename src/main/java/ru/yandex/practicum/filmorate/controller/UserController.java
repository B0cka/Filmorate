package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.inMemoryUserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    final inMemoryUserStorage inMemoryUserStorage;
    @Autowired
    public UserController(inMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return inMemoryUserStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return inMemoryUserStorage.update(newUser);
    }

    @GetMapping
    public Collection<User> getAll() {
        return inMemoryUserStorage.getAll();
    }


}
