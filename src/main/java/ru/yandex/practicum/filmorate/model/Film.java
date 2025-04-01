package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    private Set<Long> likes = new HashSet<>();
    private Set<Long> genres = new HashSet<>();

    String mpaRatings;
    public Set<Long> getLikes() {
        return likes;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLikes(Set<Long> likes) {
        this.likes = likes;
    }

    public Set<Long> getGenres() {
        return genres;
    }

    public void setGenres(Set<Long> genres) {
        this.genres = genres;
    }

    public String getMpaRatings() {
        return mpaRatings;
    }

    public void setMpaRatings(String mpa_ratings) {
        this.mpaRatings = mpa_ratings;
    }
}
