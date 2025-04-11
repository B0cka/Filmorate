package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewStorage reviewStorage;

    public ReviewController(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    @PostMapping()
    public Review createReview(@RequestBody Review review){
        return reviewStorage.createReview(review);
    }

    @PutMapping()
    public Review updateReview(@RequestBody Review review){
        return reviewStorage.updateReview(review);
    }

    @DeleteMapping("/{id}")
    void deleteReview(@PathVariable Long id){
        reviewStorage.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Optional<Review> getById(@PathVariable Long id) {
        return Optional.ofNullable(reviewStorage.getById(id));
    }

    @GetMapping
    public List<Review> getAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") Integer count
    ) {
        return reviewStorage.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        reviewStorage.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewStorage.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewStorage.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewStorage.removeDislike(id, userId);
    }

}

