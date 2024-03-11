package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    @NotBlank
    @Size(max = 70)
    private final String name;
    @Size(max = 200)
    @NotNull
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @Positive
    private final long duration;
    @NotNull
    private final MpaRating mpa;
    private int id;
    private Set<Director> directors;

    private Set<Integer> likeUserIds = Collections.emptySet();
    private Set<Genre> genres = Collections.emptySet();
}
