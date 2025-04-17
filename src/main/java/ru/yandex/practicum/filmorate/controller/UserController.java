package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FeedRecord;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

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

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
       return userService.getRecommendations(id);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable Long userId) {
         userService.removeUser(userId);
    }

    @GetMapping("/{userId}/feed")
    public Collection<FeedRecord> getFeed(@PathVariable Long userId) {
        return userService.getFeed(userId);
    }

}

