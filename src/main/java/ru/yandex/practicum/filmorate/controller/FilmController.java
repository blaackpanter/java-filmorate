package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавляем фильм");
        final Film add = filmService.add(film);
        log.info("Фильм успешно добавлен");
        return add;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновим информацию о фильме");
        final Film update = filmService.update(film);
        log.info("Информация о фильме успешно обновлена");
        return update;
    }

    @GetMapping
    public List<Film> getAllFilmsList() {
        log.info("Возвращаем список фильмов");
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required = false, defaultValue = "0") int count) {
        if (count == 0) {
            count = 10;
        }
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}")
    public Film getFilms(@PathVariable("id") int id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirectorIdSorted(
            @PathVariable String directorId,
            @RequestParam(defaultValue = "year") String sortBy
    ) {
        log.info("Получение фильмов режиссера с ID: {} отсортированному по: {}", directorId, sortBy);
        return filmService.getFilmsByDirectorIdSorted(directorId, sortBy);
    }
}
