package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;


@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(ReviewStorage reviewStorage,UserStorage userStorage, FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review createReview(Review review) {
        validateReview(review);
        log.info("Создан отзыв: {}", review);

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

        return reviewStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + id + " не найден"));
    }


    public void deleteReview(Long id) {
        reviewStorage.deleteReview(id);
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        reviewStorage.addLike(reviewId, userId);
        log.info("Пользователь {} лайкнул отзыв {}", userId, reviewId);
    }

    public void createDislike(Long reviewId, Long userId) {
        reviewStorage.addDislike(reviewId, userId);
        log.info("Пользователь {} дизлайкнул отзыв {}", userId, reviewId);
    }

    public void removeLike(Long reviewId, Long userId) {
        reviewStorage.removeLike(reviewId, userId);
        log.info("Пользователь {} удалил лайк {}", userId, reviewId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        reviewStorage.removeDislike(reviewId, userId);
        log.info("Пользователь {} удалил дизлайк {}", userId, reviewId);
    }


    private void validateReview(Review review) {

        if (userStorage.getById(review.getUserId()) == null) {
            throw new UserNotFoundException("Юзера с Id не существует!: " + review.getUserId());
        }
        if (filmStorage.getById(review.getFilmId()) == null) {
            throw new FilmNotFoundException("film с Id не существует!: " + review.getFilmId());
        }
        if (review.getFilmId() == null || review.getFilmId() <= 0) {
            throw new FilmNotFoundException("Некорректный filmId: " + review.getFilmId());
        }
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Контент отзыва не может быть пустым.");
        }
        if (review.getIsPositive() == null) {
            throw new IllegalArgumentException("Поле isPositive обязательно.");
        }
        if (review.getUserId() == null || review.getUserId() <= 0) {
            throw new UserNotFoundException("Некорректный userId: " + review.getUserId());
        }
    }
}
