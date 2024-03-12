package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director save(Director director) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    final PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO directors (name) VALUES (?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, director.getName());
                    return ps;
                },
                keyHolder);
        final Integer id = keyHolder.getKeyAs(Integer.class);
        return get(id);
    }

    @Override
    public Director get(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, name FROM directors WHERE id = ?",
                    new Object[]{id},
                    (rs, num) -> Director.builder().id(rs.getInt(1)).name(rs.getString(2)).build());
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException(String.format("Директор с id %d не найден", id));
        }
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(
                "SELECT id, name FROM directors",
                (rs, num) -> Director.builder().id(rs.getInt(1)).name(rs.getString(2)).build()
        );
    }


    @Override
    public Director update(Director director) {
        try {
            jdbcTemplate.queryForObject(
                    "SELECT id FROM directors WHERE id = ?",
                    new Object[]{director.getId()},
                    (rs, num) -> rs.getInt("id")
            );
            jdbcTemplate.update(
                    "UPDATE directors SET name = ? WHERE id = ?",
                    director.getName(), director.getId()
            );
            return director;
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException(String.format("Режиссер с ID %d не найден", director.getId()));
        }
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(
                "DELETE FROM film_directors WHERE director_id = ?",
                id);
        jdbcTemplate.update(
                "DELETE FROM directors WHERE id = ?",
                id
        );


    }
}
