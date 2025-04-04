package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (id, name, email, birthday, login) VALUES (?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, user.getId(), user.getName(), user.getEmail(), user.getBirthday(), user.getLogin());
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении пользователя: " + e.getMessage());
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public User getById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            System.err.println("Пользователь с id=" + id + " не найден!");
            return null;
        }
    }


    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        try {
            int updatedRows = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

            if (updatedRows == 0) {
                throw new RuntimeException("Пользователь с id=" + user.getId() + " не найден!");
            }

            return getById(user.getId());
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении пользователя: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    public void sendFriendRequest(Long userId, Long friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'PENDING')";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void confirmFriendRequest(Long userId, Long friendId) {
        String sql = "UPDATE friendships SET status = 'CONFIRMED' WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, friendId, userId);
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);
    }

    public List<User> getFriends(Long userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendships f ON (u.id = f.friend_id OR u.id = f.user_id) " +
                "WHERE (f.user_id = ? OR f.friend_id = ?) AND f.status = 'CONFIRMED'";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId, userId);

    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}