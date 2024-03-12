package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    Film get(int id);

    Film update(Film film);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> getCommonFilms(int firstUserId, int secondUserId);

    List<Film> findByDirectorIdAndSortBy(String directorId, String sortBy);
}
