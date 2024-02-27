package ru.yandex.practicum.filmorate.service.film;

public class WrongFilmDateException extends RuntimeException {
    public WrongFilmDateException(String message) {
        super(message);
    }
}
