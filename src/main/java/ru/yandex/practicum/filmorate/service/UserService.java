package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить себя в друзья!");
        }

        User user = inMemoryUserStorage.getById(userId);
        User friend = inMemoryUserStorage.getById(friendId);

        if (user.getFriends().contains(friendId)) {
            throw new ValidationException("Пользователь уже в друзьях!");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя удалить себя из друзей!");
        }

        User user = inMemoryUserStorage.getById(userId);
        User friend = inMemoryUserStorage.getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = inMemoryUserStorage.getById(userId);
        User otherUser = inMemoryUserStorage.getById(otherUserId);

        Set<Long> userFriends = new HashSet<>(user.getFriends());
        Set<Long> otherUserFriends = new HashSet<>(otherUser.getFriends());

        // Оставляем только общие ID друзей
        userFriends.retainAll(otherUserFriends);

        // Преобразуем ID в объекты User и возвращаем
        return userFriends.stream()
                .map(inMemoryUserStorage::getById)
                .collect(Collectors.toList());
    }

}
