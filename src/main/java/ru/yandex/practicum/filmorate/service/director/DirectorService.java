package ru.yandex.practicum.filmorate.service.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAllDirectors() {
        log.info("Fetching all directors");
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(String id) {
        int idToInteger;
        try {
            idToInteger = Integer.parseInt(id);
        } catch (Exception ex) {
            throw new RuntimeException("Id должен быть числом");
        }
        log.info("Получение режиссера с ID: {}", id);
        return directorStorage.get(idToInteger);
    }

    public Director createDirector(Director director) {
        log.info("Создание режиссера: {}", director.getName());
        return directorStorage.save(director);
    }

    public Director updateDirector(Director director) {
        log.info("Обновление режиссера с ID: {}", director.getId());
        return directorStorage.update(director);
    }

    public void deleteDirector(String id) {
        int idToInteger;
        try {
            idToInteger = Integer.parseInt(id);
        } catch (Exception ex) {
            throw new RuntimeException("Id должен быть числом");
        }
        log.info("Удаление режиссера с ID: {}", id);
        directorStorage.delete(idToInteger);
    }
}

