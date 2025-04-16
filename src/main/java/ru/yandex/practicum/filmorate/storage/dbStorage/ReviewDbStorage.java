package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.FeedEventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.FeedOperationType.ADD;
import static ru.yandex.practicum.filmorate.model.FeedOperationType.REMOVE;
import static ru.yandex.practicum.filmorate.model.FeedOperationType.UPDATE;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedDbStorage feedStorage;

    @Override
    public Review createReview(Review review) {
        String sql = "INSERT INTO reviews (user_id, film_id, is_positive, content) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        if (review.getUseful() == null) {
            review.setUseful(0);
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, review.getUserId());
            ps.setLong(2, review.getFilmId());
            ps.setBoolean(3, review.getIsPositive());
            ps.setString(4, review.getContent());
            return ps;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().longValue());
        feedStorage.save(REVIEW, ADD, review.getReviewId(), review.getUserId());
        return review;
    }

    @Override
    public Review updateReview(@RequestBody Review review) {
        String sql = "UPDATE reviews SET user_id = ?, content = ?, useful = ?, is_positive = ? WHERE review_id = ?";
        if (review.getUseful() == null) {
            review.setUseful(0);
        }
        jdbcTemplate.update(sql,
                review.getUserId(),
                review.getContent(),
                review.getUseful(),
                review.getIsPositive(),
                review.getReviewId()
        );
        feedStorage.save(REVIEW, UPDATE, review.getReviewId(), review.getUserId());
        return review;
    }

    @Override
    public Optional<Review> getById(Long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.query(sql, new ReviewMapper(), id).stream().findAny();
    }


    @Override
    public void deleteReview(Long id) {
        Optional<Review> review = getById(id);
        if (review.isEmpty()) {
            return;
        }
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        feedStorage.save(REVIEW, REMOVE, id, review.get().getUserId());
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        String checkSql = "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?";
        List<Boolean> result = jdbcTemplate.query(checkSql,
                (rs, rowNum) -> rs.getBoolean("is_like"),
                reviewId, userId);

        if (!result.isEmpty()) {
            boolean isLike = result.get(0);
            if (!isLike) {
                String updateSql = "UPDATE review_likes SET is_like = true WHERE review_id = ? AND user_id = ?";
                jdbcTemplate.update(updateSql, reviewId, userId);
                updateUseful(reviewId, 2);
            }
        } else {
            String insertSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, true)";
            jdbcTemplate.update(insertSql, reviewId, userId);
            updateUseful(reviewId, 1);
        }
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        String checkSql = "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?";
        List<Boolean> result = jdbcTemplate.query(checkSql,
                (rs, rowNum) -> rs.getBoolean("is_like"),
                reviewId, userId);

        if (!result.isEmpty()) {
            boolean isLike = result.get(0);
            if (isLike) {
                String updateSql = "UPDATE review_likes SET is_like = false WHERE review_id = ? AND user_id = ?";
                jdbcTemplate.update(updateSql, reviewId, userId);
                updateUseful(reviewId, -2);
            }
        } else {
            String insertSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, false)";
            jdbcTemplate.update(insertSql, reviewId, userId);
            updateUseful(reviewId, -1);
        }
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true";
        int rows = jdbcTemplate.update(sql, reviewId, userId);
        if (rows > 0) {
            updateUseful(reviewId, -1);
        }
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false";
        int rows = jdbcTemplate.update(sql, reviewId, userId);
        if (rows > 0) {
            updateUseful(reviewId, 1);
        }
    }

    private void updateUseful(Long reviewId, int num) {
        String sql = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
        jdbcTemplate.update(sql, num, reviewId);
    }

    @Override
    public List<Review> getReviews(Long filmId, Integer count) {
        String sql;
        Object[] params;

        if (count == null || count <= 0) {
            count = 10;
        }

        if (filmId != null) {
            sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            params = new Object[]{filmId, count};
        } else {
            sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            params = new Object[]{count};
        }

        return jdbcTemplate.query(sql, new ReviewMapper(), params);
    }

    public class ReviewMapper implements RowMapper<Review> {

        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Review(
                    rs.getLong("review_id"),
                    rs.getLong("user_id"),
                    rs.getLong("film_id"),
                    rs.getString("content"),
                    rs.getBoolean("is_positive"),
                    rs.getInt("useful")
            );
        }
    }

}
