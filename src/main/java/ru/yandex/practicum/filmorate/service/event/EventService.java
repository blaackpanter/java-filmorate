package ru.yandex.practicum.filmorate.service.event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.util.List;

public interface EventService {
    List<Event> findEventsByUserId(Integer id);

    Event createAddLikeEvent(Integer userID, Integer filmId);

    Event createRemoveLikeEvent(Integer userID, Integer filmId);

    Event createReviewEvent(Integer userID, OperationType operation, Integer reviewId);

    Event createAddFriendEvent(Integer userID, Integer friendId);

    Event createRemoveFriendEvent(Integer userID, Integer friendId);
}
