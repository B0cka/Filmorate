package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;
import ru.yandex.practicum.filmorate.model.FeedRecord;

import java.util.Collection;
import java.util.Optional;

public interface FeedStorage {

    FeedRecord save(FeedEventType eventType, FeedOperationType operation, Long entityId, Long userId);

    Optional<FeedRecord> getById(Long eventId);

    Collection<FeedRecord> getFeedForUser(Long userId);
}
