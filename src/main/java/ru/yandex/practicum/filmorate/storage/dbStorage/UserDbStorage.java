package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
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
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.longValue());
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

    public List<User> getCommonFriends(Long id, Long otherId) {
        String sql = """
        SELECT u.*
        FROM users u
        JOIN friendships f1 ON u.id = f1.friend_id
        JOIN friendships f2 ON u.id = f2.friend_id
        WHERE f1.user_id = ? AND f2.user_id = ? AND f1.status = 'CONFIRMED' AND f2.status = 'CONFIRMED'
    """;

        return jdbcTemplate.query(sql, this::mapRowToUser, id, otherId);
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