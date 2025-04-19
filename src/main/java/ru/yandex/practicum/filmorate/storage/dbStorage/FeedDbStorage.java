package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;
import ru.yandex.practicum.filmorate.model.FeedRecord;
import ru.yandex.practicum.filmorate.storage.user.FeedStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public FeedRecord save(FeedEventType eventType, FeedOperationType operation, Long entityId, Long userId) {
        String sql = "INSERT INTO feed_records (user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    new String[]{"event_id"}
            );
            ps.setLong(1, userId);
            ps.setString(2, eventType.name());
            ps.setString(3, operation.name());
            ps.setLong(4, entityId);
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return getById(id).get();
    }

    @Override
    public Optional<FeedRecord> getById(Long eventId) {
        return jdbcTemplate.query(
                "SELECT * FROM feed_records WHERE event_id = ?",
                this::mapRowToFeedRecord,
                eventId
        ).stream().findAny();
    }

    @Override
    public Collection<FeedRecord> getFeedForUser(Long userId) {
        String sql = "SELECT fr.timestamp, fr.user_id, fr.event_type, fr.operation, " +
                "fr.event_id, fr.entity_id " +
                "FROM feed_records fr " +
                "WHERE fr.user_id = ? " +
                "ORDER BY fr.timestamp ASC";

        return jdbcTemplate.query(
                sql,
                this::mapRowToFeedRecord,
                userId
        );
    }

    private FeedRecord mapRowToFeedRecord(ResultSet rs, int rowNum) throws SQLException {
        return new FeedRecord(
                rs.getLong("event_id"),
                rs.getTimestamp("timestamp").getTime(),
                FeedEventType.valueOf(rs.getString("event_type")),
                FeedOperationType.valueOf(rs.getString("operation")),
                rs.getLong("entity_id"),
                rs.getLong("user_id")
        );
    }
}
