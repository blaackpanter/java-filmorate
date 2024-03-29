package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;

    @Email
    @NotBlank
    @Size(max = 128)
    private final String email;

    @NotBlank
    @Size(max = 128)
    private final String login;

    private String name;

    @NotNull
    @PastOrPresent
    private final LocalDate birthday;

    private Set<Integer> friends = Collections.emptySet();
}
