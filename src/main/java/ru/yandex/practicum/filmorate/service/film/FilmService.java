package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchBy;

import java.util.List;

public interface FilmService {

    Film add(Film film);

    Film getFilm(int id);

    Film update(Film film);

    List<Film> getAllFilms();

    boolean addLike(int id, int userId);

    boolean deleteLike(int id, int userId);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> getCommonFilms(Integer firstUserId, Integer secondUserId);

    List<Film> getFilmsByDirectorIdSorted(String directorId, String sortBy);

    boolean deleteFilm(int id);

    List<Film> getRecommendedFilms(int userId);

    List<Film> getFilmsWithQuery(String query, List<SearchBy> search);
}
