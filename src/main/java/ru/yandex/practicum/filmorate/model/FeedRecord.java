package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedRecord {
    private Long eventId;
    private Long timestamp;
    private FeedEventType eventType;
    private FeedOperationType operation;
    private Long entityId;
    private Long userId;
}
