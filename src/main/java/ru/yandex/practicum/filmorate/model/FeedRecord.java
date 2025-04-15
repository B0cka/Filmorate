package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FeedRecord {
    Long eventId;
    LocalDateTime timestamp;
    FeedEventType eventType;
    FeedOperationType operation;
    Long entityId;
    Long userId;
}
