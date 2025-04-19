DELETE FROM film_directors;
DELETE FROM directors;
ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1;

DELETE FROM films;
ALTER TABLE films ALTER COLUMN id RESTART WITH 1;

DELETE FROM users;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

DELETE FROM mpa_ratings;
ALTER TABLE mpa_ratings ALTER COLUMN id RESTART WITH 1;
INSERT INTO mpa_ratings (mpa_name) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

DELETE FROM genres;
ALTER TABLE genres ALTER COLUMN id RESTART WITH 1;
INSERT INTO genres (name) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

DELETE FROM reviews;
ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1;

DELETE FROM feed_records;
ALTER TABLE feed_records ALTER COLUMN event_id RESTART WITH 1;