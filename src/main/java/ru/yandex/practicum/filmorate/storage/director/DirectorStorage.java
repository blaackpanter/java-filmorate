package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director get(int id);

    List<Director> getAllDirectors();

    Director save(Director director);

    void delete(int id);

    Director update(Director director);
}

