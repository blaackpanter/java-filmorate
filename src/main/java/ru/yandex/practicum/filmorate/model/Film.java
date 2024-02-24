package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
public class Film {

    private int id;

    @NotBlank
    private final String name;

    @Size(max = 200)
    @NotNull
    private final String description;

    @NotNull
    private final LocalDate releaseDate;

    @PositiveOrZero
    private final long duration;
}
