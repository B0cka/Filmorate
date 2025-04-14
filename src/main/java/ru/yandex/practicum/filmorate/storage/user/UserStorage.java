package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User create(@RequestBody User user);

    User update(@RequestBody User newUser);

    Collection<User> getAll();

    User getById(Long id);

    void sendFriendRequest(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

    List<User> getCommonFriends(Long id, Long otherId);

    List<User> getFriends(Long id);

    public boolean existsById(long id);

}
