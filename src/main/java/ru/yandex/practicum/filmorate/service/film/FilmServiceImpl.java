package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchBy;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.event.EventServiceImpl;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final EventServiceImpl eventServiceImpl;

    @Autowired
    public FilmServiceImpl(
            FilmStorage filmStorage,
            UserService userService,
            EventServiceImpl eventServiceImpl) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.eventServiceImpl = eventServiceImpl;
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
            throw new NotFoundException("Фильмов не найдено, чтобы обновить сначала необходимо добавить фильм");
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
            throw new NotFoundException("Невозможно поставить лайк пользователем, которого не существует");
        }
        final Film film = filmStorage.get(id);
        if (film.getLikeUserIds().contains(userId)) {
            eventServiceImpl.createAddLikeEvent(userId, id);
            return false;
        }
        final Set<Integer> likeUserIds = new HashSet<>(film.getLikeUserIds());
        likeUserIds.add(userId);
        film.setLikeUserIds(Set.copyOf(likeUserIds));
        filmStorage.update(film);
        eventServiceImpl.createAddLikeEvent(userId, id);
        return true;
    }

    @Override
    public boolean deleteLike(int id, int userId) {
        if (!userService.userExist(userId)) {
            throw new NotFoundException("Невозможно поставить лайк пользователем, которого не существует");
        }
        final Film film = filmStorage.get(id);
        if (!film.getLikeUserIds().contains(userId)) {
            return false;
        }
        final Set<Integer> likeUserIds = new HashSet<>(film.getLikeUserIds());
        likeUserIds.remove(userId);
        film.setLikeUserIds(Set.copyOf(likeUserIds));
        filmStorage.update(film);
        eventServiceImpl.createRemoveLikeEvent(userId, id);
        return true;
    }

    @Override
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    @Override
    public List<Film> getRecommendedFilms(int userId) {
        List<Film> films = getAllFilms();

        User user = userService.getUser(userId);
        User other = userService.getMatchedUser(films, user);

        if (other == null)
            return Collections.emptyList();

        List<Film> otherFilms = films.stream()
                .filter(film -> film.getLikeUserIds().contains(other.getId()))
                .collect(Collectors.toList());
        List<Film> userFilms = films.stream()
                .filter(film -> film.getLikeUserIds().contains(user.getId()))
                .collect(Collectors.toList());

        otherFilms.removeAll(userFilms);

        return otherFilms;
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

    @Override
    public List<Film> getFilmsByDirectorIdSorted(String directorId, String sortBy) {
        return filmStorage.findByDirectorIdAndSortBy(directorId, sortBy);
    }

    @Override
    public boolean deleteFilm(int id) {
        return filmStorage.deleteFilm(id);
    }

    @Override
    public List<Film> getFilmsWithQuery(String query, List<SearchBy> search) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }
        List<Film> filmsWithQuery = filmStorage.getFilmsWithQuery(query, search);
        return filmsWithQuery.stream()
                .sorted((o1, o2) -> -Integer.compare(o1.getLikeUserIds().size(), o2.getLikeUserIds().size()))
                .collect(Collectors.toList());
    }
}
