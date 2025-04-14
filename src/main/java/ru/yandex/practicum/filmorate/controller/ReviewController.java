package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping()
    public Review createReview(@RequestBody Review review) {
        if (review.getUserId() < 0 || review.getFilmId() < 0) {
            throw  new UserNotFoundException("Неверные данные");
        } else {
            return reviewService.createReview(review);
        }

    }

    @PutMapping()
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") Integer count
    ) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.createDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
    }

}

