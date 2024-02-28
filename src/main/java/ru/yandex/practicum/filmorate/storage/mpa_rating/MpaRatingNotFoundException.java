package ru.yandex.practicum.filmorate.storage.mpa_rating;

public class MpaRatingNotFoundException extends RuntimeException {
    public MpaRatingNotFoundException(String message) {
        super(message);
    }
}
