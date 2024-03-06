package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    Film get(int id);

    Film update(Film film);

    List<Film> getAllFilms();

    List<Film> getFilmsSortByLike(int limit);

    List<Film> getCommonFilms(int firstUserId, int secondUserId);
}
