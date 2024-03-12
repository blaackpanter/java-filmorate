package ru.yandex.practicum.filmorate.model.enums;

import lombok.Getter;

@Getter
public enum OperationType {
    REMOVE("REMOVE"), ADD("ADD"), UPDATE("UPDATE");
    private final String title;

    OperationType(String title) {
        this.title = title;
    }

}
