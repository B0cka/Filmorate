DELETE FROM users;
DELETE FROM mpa_ratings;
ALTER TABLE mpa_ratings ALTER COLUMN id RESTART WITH 1;
INSERT INTO mpa_ratings (mpa_name) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

DELETE FROM genres;
ALTER TABLE genres ALTER COLUMN id RESTART WITH 1;
INSERT INTO genres (name) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

DELETE FROM directors;
ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1;
INSERT INTO directors (name) VALUES ( 'Кристофер Нолан' ), ('Уве Болл'), ('Сарик Андреасян'), ('Джордж Лукас');