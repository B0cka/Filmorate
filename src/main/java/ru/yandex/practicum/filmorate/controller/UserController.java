package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public User create(@RequestBody User user) {
        userService.validateUser(user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        userService.validateUser(newUser);
        return userService.update(newUser);
    }

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

}

