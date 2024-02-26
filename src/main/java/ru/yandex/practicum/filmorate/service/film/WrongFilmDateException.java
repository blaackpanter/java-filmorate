package ru.yandex.practicum.filmorate.service.film;

public class WrongFilmDateException extends Exception {
    public WrongFilmDateException(String message) {
        super(message);
    }
}
