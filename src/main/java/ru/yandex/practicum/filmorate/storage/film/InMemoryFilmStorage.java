package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.WrongFilmDateException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) throws WrongFilmDateException {
        int id = films.size() + 1;
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film get(int id) throws FilmNotFoundException {
        final Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException(String.format("Не найдено фильма с id = %s", film.getId()));
        }
        return film;
    }

    @Override
    public Film update(Film film) throws WrongFilmDateException, FilmNotFoundException {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(String.format("Не найдено фильма с id = %s", film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return List.copyOf(films.values());
    }

    @Override
    public List<Film> getFilmsSortByLike(int limit) {
        return films.values().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikeUserIds().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
