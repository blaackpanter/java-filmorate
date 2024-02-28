package ru.yandex.practicum.filmorate.storage.film;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(String message) {
        super(message);
    }
}
