package ru.yandex.practicum.filmorate.model.enums;

import lombok.Getter;

@Getter
public enum EventType {
    LIKE("LIKE"), REVIEW("REVIEW"), FRIEND("FRIEND");
    private final String title;

    EventType(String title) {
        this.title = title;
    }

}
