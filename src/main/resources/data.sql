DELETE FROM mpa_ratings;
INSERT INTO mpa_ratings (name) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

DELETE FROM genres;
INSERT INTO genres (name) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');