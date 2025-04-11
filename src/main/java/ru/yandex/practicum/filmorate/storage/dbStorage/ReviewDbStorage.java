package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.*;
import java.util.List;

@Repository
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

        return review;
    }

    @Override
    public Review updateReview(@RequestBody Review review) {
        String sql = "UPDATE reviews SET user_id = ?, content = ?, useful = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getUserId(),
                review.getContent(),
                review.getUseful(),
                review.getIsPositive(),
                review.getReviewId()
        );
        return review;
    }

    @Override
    public Review getById(Long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, new ReviewMapper(), id);
    }

    @Override
    public void deleteReview(Long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
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

        return jdbcTemplate.query(sql, params, new ReviewMapper());
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
