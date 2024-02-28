package ru.yandex.practicum.filmorate.storage.genre;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(String message) {
        super(message);
    }
}
