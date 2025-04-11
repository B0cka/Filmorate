package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Review {

    private Long reviewId;
    private Long userId;
    private Long filmId;
    private String content;
    private Boolean isPositive;
    private Integer useful;

    public Review(Long reviewId, Long userId, Long filmId, String content, Boolean isPositive, Integer useful) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.filmId = filmId;
        this.content = content;
        this.isPositive = isPositive;
        this.useful = useful;
    }

    public Review() {
    }
}
