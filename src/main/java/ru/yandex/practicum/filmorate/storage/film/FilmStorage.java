package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.WrongFilmDateException;

import java.util.List;

public interface FilmStorage {

    Film add(Film film) throws WrongFilmDateException;

    Film get(int id) throws FilmNotFoundException;

    Film update(Film film) throws WrongFilmDateException, FilmNotFoundException;

    List<Film> getAllFilms();

    List<Film> getFilmsSortByLike(int limit);
}
