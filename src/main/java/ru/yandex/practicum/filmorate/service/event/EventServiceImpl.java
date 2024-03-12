package ru.yandex.practicum.filmorate.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.time.Instant;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    private final EventStorage eventStorage;

    @Autowired
    public EventServiceImpl(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public List<Event> findEventsByUserId(Integer id) {
        return eventStorage.findEventsByUserID(id);
    }

    private Event createEvent(Integer userID, EventType eventType, OperationType operation, Integer entityId) {
        Event event = new Event();
        event.setTimestamp(Instant.now().toEpochMilli());
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setUserId(userID);
        event.setEntityId(entityId);

        return eventStorage.createEvent(event);
    }

    public Event createAddLikeEvent(Integer userID, Integer filmId) {
        return createEvent(userID, EventType.LIKE, OperationType.ADD, filmId);
    }

    public Event createRemoveLikeEvent(Integer userID, Integer filmId) {
        return createEvent(userID, EventType.LIKE, OperationType.REMOVE, filmId);
    }

    public Event createReviewEvent(Integer userID, OperationType operation, Integer reviewId) {
        return createEvent(userID, EventType.REVIEW, operation, reviewId);
    }

    public Event createAddFriendEvent(Integer userID, Integer friendId) {
        return createEvent(userID, EventType.FRIEND, OperationType.ADD, friendId);
    }

    public Event createRemoveFriendEvent(Integer userID, Integer friendId) {
        return createEvent(userID, EventType.FRIEND, OperationType.REMOVE, friendId);
    }
}