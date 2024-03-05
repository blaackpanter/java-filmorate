package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class Genre {
    private int id;
    @EqualsAndHashCode.Exclude
    private final String name;
}
