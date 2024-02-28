package ru.yandex.practicum.filmorate.service.mpa_rating;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingService {
    MpaRating get(int id);

    List<MpaRating> getAll();
}
