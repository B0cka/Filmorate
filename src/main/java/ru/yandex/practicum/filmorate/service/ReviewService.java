package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review createReview(Review review) {
        validateReview(review);
        log.info("Создан отзыв: {}");
        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        validateReview(review);
        log.info("Обновление отзыва: {}");
        return reviewStorage.updateReview(review);
    }

    public Review getReviewById(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Некорректный ID");
        }
        Review review = reviewStorage.getById(id);
        if (review == null) {
            throw new ReviewNotFoundException("Отзыв с ID " + id + " не найден");
        }
        return review;
    }


    public void deleteReview(Long id) {
        reviewStorage.deleteReview(id);
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    private void validateReview(Review review) {
        if (review.getUserId() == null || review.getUserId() <= 0) {
            throw new IllegalArgumentException("Некорректный userId: " + review.getUserId());
        }
        if (review.getFilmId() == null || review.getFilmId() <= 0) {
            throw new IllegalArgumentException("Некорректный filmId: " + review.getFilmId());
        }
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Контент отзыва не может быть пустым.");
        }
        if (review.getIsPositive() == null) {
            throw new IllegalArgumentException("Поле isPositive обязательно.");
        }
    }
}
