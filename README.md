# Filmorate Database Schema 📽️

## 📌 Описание

### Это схема базы данных для проекта Filmorate. Тут хранятся пользователи, фильмы, жанры, возрастные рейтинги и дружеские связи между юзерами.

![фото диаграммы(db_diagram.png)](https://pdf-service-lucidchart-com.s3.amazonaws.com/a6563edf-d47b-407e-a0b8-ccf4d14ef2bb?X-Amz-Security-Token=IQoJb3JpZ2luX2VjECQaCXVzLWVhc3QtMSJGMEQCIHxPxclUAP8gAkF%2BPBF7VLybhb1ocrbLvqkrFYak5hkLAiBxeq6CmZta1f5KFYSbkJ26dBHrdnhzQM9vTvHT4h1fTCq6BQiM%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAAaDDkzNTYwNjkyNjE4MiIM8nukc53lnSA0xoghKo4FM5Qx44Ggpbf73ZcPdRgsG2JIzHr0ASLTPq%2FPKYtEiochQIW29HeuxxfJgy1FxwzBILlFni1R25gFRZHWREfv6H38GIKh9ioYN1yXi379Ii%2FN49GO3w0MvdRpXB%2FZ%2FMZIbVB45vPx0VwicCY%2BoBN2rEb0CU9dWVD3C1b8CzA0g8WfLYvwuO1RaDjNObxVXRyVp%2BbqRpqKyn7Bx8eoZaRpxWMno87G4ryIPjKCtNZZUIgDgx0SHIPT8Wr03HtG1i3n8a4fQ6QoXPoSvFQ8f4J9R%2FNkXUMFppF9DJGfoWCbfWiqrCAmO5Ugi6%2Fjm7S%2FgJENOj98FRsRrPyTcGp29FRYk5wDm5wl97B74pqgl4bzvvEIbfP%2FaDYI4a6iDPO4GMcU28%2FrFShw5RLntMaDbI9SFzzApR%2Fx2AIoLMu2VvudY21T3mV99JImsgfA%2BeyaYKch0Pe2MH1Eem%2FVBXDz3x8Tf%2FZX4no%2By0RwvqsFaNGPZpCtBiueyxlhaJSdrrjAGCSVWMkDZZEYC4e2n6wa6AzB6L08FS%2BjMsLXYmPnaLj%2F9OmwFBMuOiLIBq4Ct%2BvnlIYN6O1jONHuPBTU%2BFtOrf3ycbX5PTdCpdQnHpSgUmpPfLixYoZlLi9NFCbQalvTjVtNmQDNnQN3CRgjon1K1SRw3KwX%2FSAab7Ib4j%2BFKWPipX4GJ%2FHYBtERbmlU2BplaMe6ctr1APHHs8fQMOeq4OaYBf8k6uagPEat7%2Ba8F9Pok7pnsB2Zx9SEhHR4hjx82gz7JXYIVih%2BgbWgW3O756JLudRrXgQg7tbtf2InwPQPt4w4OLq4pn%2B4VO4XSuIpqW0%2BoRSiVmXVasBWJ9eSth4h6Eu9dmG3xw6Aax4t9hiMMPPPpL8GOrIBis35lOL5zN4Gss47xuEHdfdCaUo897RwAcxdwbwKnpe9VTcUBr4uAIfxeBVbf4egFXqKOja5mUuDK3XJ9EYWbzhsYDwhT49yCfLIQePuDoPiDAHkiVqe4Ziut6QQHzFeJ509SrlbznGsyv2kE5fMAIx%2FMRvxnsKY%2FazceaiK2Pv0hqyxQGFuy0XxM6SNTzm7ywKvM0k6g1WzWp7jDl42%2FWI4TF0yGrNfUCvswHo%2FkS8LUg%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250330T120232Z&X-Amz-SignedHeaders=host&X-Amz-Expires=86399&X-Amz-Credential=ASIA5TVUEXNTCA4HJGWT%2F20250330%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=15897720ab3263fffdec9a37beb131bd793f74d86306ab8ccec012dc8cf42391)

## 📊 Структура таблиц

* ### users — пользователи с их данными.

* ### films — фильмы с описанием, рейтингом и продолжительностью.

* ### genres — список жанров.

* ### mpa_ratings — возрастные рейтинги фильмов.

* ### film_genres — связь фильмов и жанров (многие ко многим).

* ### friendships — дружба между пользователями (подтвержденная и неподтвержденная).

## 🔍 Примеры SQL-запросов

### 1. Получить всех пользователей

``` 
SELECT * FROM users;
```

### 2.Получить фильмы определенного жанра (например, "Комедия")

``` 
SELECT f.* FROM films f
JOIN film_genres fg ON f.id = fg.film_id
JOIN genres g ON fg.genre_id = g.id
WHERE g.name = 'Комедия';
```

### 3.Получить список друзей пользователя (например, id = 1)

```
SELECT u.id, u.name, f.status
FROM friendships f
JOIN users u ON f.friend_id = u.id
WHERE f.user_id = 1;
```
