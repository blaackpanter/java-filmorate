package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Director {

    @Min(0)
    private Integer id;
    @NotBlank
    private String name;
}

