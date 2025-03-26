package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.inMemoryUserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    final inMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(inMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addFriend(Long userId, Long friendId) {

        User user = inMemoryUserStorage.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден!");
        }

        User friend = inMemoryUserStorage.getById(friendId);
        if (friend == null) {
            throw new UserNotFoundException("Пользователь с id " + friendId + " не найден!");
        }


        if (user.getFriends() != null && user.getFriends().contains(friendId)) {
            throw new ValidationException("Пользователь уже в друзьях!");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }


    public List<User> getFriend(Long id) {
        User user = inMemoryUserStorage.getById(id); // Получаем пользователя
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        // Получаем список друзей
        return user.getFriends().stream()
                .map(inMemoryUserStorage::getById) // Преобразуем ID в объекты User
                .collect(Collectors.toList());
    }


    public void removeFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null || userId <= 0 || friendId <= 0) {
            throw new ValidationException("Некорректные данные: userId и friendId должны быть положительными числами (больше 0)");
        }


        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя удалить себя из друзей!");
        }

        User user = inMemoryUserStorage.getById(userId);
        User friend = inMemoryUserStorage.getById(friendId);

        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден!");
        }
        if (friend == null) {
            throw new UserNotFoundException("Пользователь с id " + friendId + " не найден!");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        if (userId == null || otherUserId == null || userId <= 0 || otherUserId <= 0) {
            throw new ValidationException("Некорректные данные: userId и otherUserId должны быть положительными числами");
        }

        User user = inMemoryUserStorage.getById(userId);
        User otherUser = inMemoryUserStorage.getById(otherUserId);

        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден!");
        }
        if (otherUser == null) {
            throw new UserNotFoundException("Пользователь с id " + otherUserId + " не найден!");
        }

        Set<Long> userFriends = new HashSet<>(user.getFriends());
        Set<Long> otherUserFriends = new HashSet<>(otherUser.getFriends());

        userFriends.retainAll(otherUserFriends);

        return userFriends.stream()
                .map(inMemoryUserStorage::getById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
