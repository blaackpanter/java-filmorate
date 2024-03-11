package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("in_memory")
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public Film add(Film film) {
        id += 1;
        film.setId(id);
        film.setLikeUserIds(Collections.emptySet());
        films.put(id, film);
        return film;
    }

    @Override
    public Film get(int id) {
        final Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException(String.format("Не найдено фильма с id = %s", id));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
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
                .sorted(
                        Comparator.<Film>comparingInt(film -> film.getLikeUserIds().size())
                                .reversed()
                )
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getCommonFilms(int firstUserId, int secondUserId) {
        return null;
    }

    @Override
    public List<Film> findByDirectorIdAndSortBy(String directorId, String sortBy) {
        throw new RuntimeException("No such implementation");
    }

    @Override
    public boolean deleteFilm(int id) {
        return films.remove(id) != null;
    }
}
