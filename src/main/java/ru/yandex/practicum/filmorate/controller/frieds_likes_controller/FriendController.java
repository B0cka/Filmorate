package ru.yandex.practicum.filmorate.controller.frieds_likes_controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.inMemoryUserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
public class FriendController {

    final inMemoryUserStorage inMemoryUserStorage;
    final UserService filmService;

    @Autowired
    public FriendController(inMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.filmService = userService;
    }

    @GetMapping("/{id}")
    public User getUserById(long id) {
        return inMemoryUserStorage.getById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return filmService.getFriend(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        filmService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        filmService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return filmService.getCommonFriends(id, otherId);
    }
}
