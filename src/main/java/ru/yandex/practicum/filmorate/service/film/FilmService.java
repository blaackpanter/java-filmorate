package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserNotFoundException;

import java.util.List;

public interface FilmService {

    Film add(Film film) throws WrongFilmDateException;

    Film getFilm(int id) throws FilmNotFoundException;

    Film update(Film film) throws WrongFilmDateException, FilmNotFoundException;

    List<Film> getAllFilms();

    boolean addLike(int id, int userId) throws UserNotFoundException, FilmNotFoundException, WrongFilmDateException;

    boolean deleteLike(int id, int userId) throws UserNotFoundException, FilmNotFoundException, WrongFilmDateException;

    List<Film> getPopularFilms(int count);
}
