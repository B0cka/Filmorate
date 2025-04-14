package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    private Long reviewId;
    private Long userId;
    private Long filmId;
    private String content;
    private Boolean isPositive;
    private Integer useful;

}
