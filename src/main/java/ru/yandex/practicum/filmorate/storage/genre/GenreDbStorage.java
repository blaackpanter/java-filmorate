package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre get(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    String.format("SELECT id, name FROM genres WHERE id = %s", id),
                    (rs, num) -> extractGenre(rs)
            );
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(String.format("Не найдено пользователя с id = %s", id));
        }
    }

    private Genre extractGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt(1))
                .name(rs.getString(2))
                .build();
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query(
                con -> con.prepareStatement("SELECT id, name FROM genres"),
                (rs, rowNum) -> extractGenre(rs)
        );
    }
}
