package ru.yandex.practicum.filmorate.storage.film;

public class FilmNotFoundException extends Exception {
    public FilmNotFoundException(String message) {
        super(message);
    }
}
