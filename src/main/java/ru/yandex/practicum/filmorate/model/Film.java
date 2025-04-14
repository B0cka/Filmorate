package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private MpaRating mpa;
    private Set<Director> directors;


    public Film(long id, String name, String description, LocalDate releaseDate,
              int duration, MpaRating mpa, Set<Genre> genres, Set<Director> directors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres != null ? genres : new LinkedHashSet<>();
        this.directors = directors != null ? directors : new HashSet<>();
    }
}
