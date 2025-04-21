package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Set<Director> getAllDirectors() {
        log.info("Возвращаем список всех режиссёров");
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(long id) {
        log.info("Возвращаем директора по ID");
        checkDirectorIdOrThrow(id);
        return directorStorage.getDirectorById(id).get();
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        log.info("Обновляем режиссера");
        checkDirectorIdOrThrow(director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(long directorId) {
        log.info("Удаляем режиссера");
        directorStorage.deleteDirector(directorId);
    }

    private void checkDirectorIdOrThrow(Long id) {
        directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Директор с id " + id + " не найден"));
    }
}
