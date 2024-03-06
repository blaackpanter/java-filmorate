package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserNotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FilmServiceImpl implements FilmService {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmServiceImpl(
            FilmStorage filmStorage,
            UserService userService
    ) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public Film add(Film film) {
        checkFilmReleaseDate(film);
        return filmStorage.add(film);
    }

    @Override
    public Film getFilm(int id) {
        return filmStorage.get(id);
    }

    @Override
    public Film update(Film film) {
        if (getAllFilms().isEmpty()) {
            throw new FilmNotFoundException("Фильмов не найдено, чтобы обновить сначала необходимо добавить фильм");
        }
        if (film.getLikeUserIds() == null) {
            film.setLikeUserIds(Collections.emptySet());
        }
        checkFilmReleaseDate(film);
        return filmStorage.update(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public boolean addLike(int id, int userId) {
        if (!userService.userExist(userId)) {
            throw new UserNotFoundException("Невозможно поставить лайк пользователем, которого не существует");
        }
        final Film film = filmStorage.get(id);
        if (film.getLikeUserIds().contains(userId)) {
            return false;
        }
        final Set<Integer> likeUserIds = new HashSet<>(film.getLikeUserIds());
        likeUserIds.add(userId);
        film.setLikeUserIds(Set.copyOf(likeUserIds));
        filmStorage.update(film);
        return true;
    }

    @Override
    public boolean deleteLike(int id, int userId) {
        if (!userService.userExist(userId)) {
            throw new UserNotFoundException("Невозможно поставить лайк пользователем, которого не существует");
        }
        final Film film = filmStorage.get(id);
        if (!film.getLikeUserIds().contains(userId)) {
            return false;
        }
        final Set<Integer> likeUserIds = new HashSet<>(film.getLikeUserIds());
        likeUserIds.remove(userId);
        film.setLikeUserIds(Set.copyOf(likeUserIds));
        filmStorage.update(film);
        return true;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilmsSortByLike(count);
    }

    private void checkFilmReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            throw new WrongFilmDateException(String.format("Дата релиза должна быть не раньше %s", MIN_DATE));
        }
    }
    @Override
    public List<Film> getCommonFilms(Integer firstUserId, Integer secondUserId) {
        return filmStorage.getCommonFilms(firstUserId, secondUserId);
    }
}
