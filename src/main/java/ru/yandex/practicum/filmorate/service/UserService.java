
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.FeedRecord;
import ru.yandex.practicum.filmorate.storage.user.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final FeedStorage feedStorage;

    public User getUserById(Long id) {
        log.info("Запрос пользователя с id={}", id);
        User user = userStorage.getById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public List<User> getFriends(Long id) {
        getUserById(id);
        return userStorage.getFriends(id);
    }

    public void addFriend(Long id, Long friendId) {
        log.info("Попытка добавить в друзья: userId={}, friendId={}", id, friendId);
        if (id.equals(friendId)) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        getUserById(id);
        getUserById(friendId);

        userStorage.sendFriendRequest(id, friendId);
        log.info("Пользователь {} отправил запрос в друзья пользователю {}", id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        getUserById(id);
        getUserById(friendId);
        userStorage.removeFriend(id, friendId);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        getUserById(id);
        getUserById(otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    public void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Имейл должен быть указан");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("В имейле должна быть @");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void getByIdForVal(Long id) {
        if (userStorage.getById(id) == null) {
            throw new UserNotFoundException(id);
        }
    }

    public boolean existsById(long id) {
        return userStorage.existsById(id);
    }

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        getByIdForVal(user.getId());
        validateUser(user);
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public void removeUser(Long id) {
        if (!userStorage.removeUser(id)) {
            log.error("Ошибка удаления пользователя id {}", id);
            throw new UserNotFoundException(id);
        }
        log.info("Пользователь с id {} удалён", id);
    }


    public List<Film> getRecommendations(Long id) {
        log.info("Получение рекомендаций для пользователя id {}", id);
        return filmDbStorage.getRecommendations(id);
    }

    public Collection<FeedRecord> getFeed(Long userId) {
        getUserById(userId);
        return feedStorage.getFeedForUser(userId);

    }
}