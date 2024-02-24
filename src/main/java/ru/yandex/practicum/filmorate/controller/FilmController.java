package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавляем фильм");
        checkFilmReleaseDate(film);
        int id = films.size() + 1;
        film.setId(id);
        films.put(id, film);
        log.info("Фильм успешно добавлен");
        return film;
    }

    @PutMapping
    public void updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновим информацию о фильме");
        if (getAllFilmsList().isEmpty()) {
            log.error("Не удалось обновить обновить информацию о фильме. Список фильмов пустой.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Фильмов не найдено, чтобы обновить сначала необходимо добавить фильм");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Не удалось обновить обновить информацию о фильме. Фильма нет в списке.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найдено фильма с id = %s".formatted(film.getId()));
        }
        checkFilmReleaseDate(film);
        films.put(film.getId(), film);
        log.info("Информация о фильме успешно обновлена");
    }

    @GetMapping
    public List getAllFilmsList() {
        log.info("Возвращаем список фильмов");
        return new ArrayList<>(films.values());
    }

    private void checkFilmReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.error("Нельзя добавить фильм с такой датой релиза");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата релиза должна быть не раньше %s".formatted(MIN_DATE));
        }
    }
}
