package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film add(Film film);

    Film getFilm(int id);

    Film update(Film film);

    List<Film> getAllFilms();

    boolean addLike(int id, int userId);

    boolean deleteLike(int id, int userId);

    List<Film> getPopularFilms(int count);
}
