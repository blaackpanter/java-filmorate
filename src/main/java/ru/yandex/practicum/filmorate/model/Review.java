package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Review.
 */
@Data
@AllArgsConstructor
public class Review {
    private Integer reviewId;
    @Size(min = 1)
    @NotBlank(message = "The content cannot be empty")
    private String content;
    @NotNull(message = "The isPositive cannot be empty")
    private Boolean isPositive;
    @NotNull(message = "The userId cannot be empty")
    private Integer userId;
    @NotNull(message = "The filmId cannot be empty")
    private Integer filmId;
    private Integer useful;
}
