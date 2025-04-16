package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedRecord {
    Long eventId;
    Long timestamp;
    FeedEventType eventType;
    FeedOperationType operation;
    Long entityId;
    Long userId;
}
