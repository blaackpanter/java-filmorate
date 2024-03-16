package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class Director {

    @Size(min = 1)
    private Integer id;
    @NotBlank
    private String name;
}

