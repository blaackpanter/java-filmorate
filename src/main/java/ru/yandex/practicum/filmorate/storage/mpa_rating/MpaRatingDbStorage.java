package ru.yandex.practicum.filmorate.storage.mpa_rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaRatingDbStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MpaRating get(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    String.format("SELECT id, name FROM mpa_ratings WHERE id = %s", id),
                    (rs, num) -> extractMpaRating(rs)
            );
        } catch (EmptyResultDataAccessException e) {
            throw new MpaRatingNotFoundException(String.format("Не найдено пользователя с id = %s", id));
        }
    }

    private MpaRating extractMpaRating(ResultSet rs) throws SQLException {
        return MpaRating.builder()
                .id(rs.getInt(1))
                .name(rs.getString(2))
                .build();
    }

    @Override
    public List<MpaRating> getAll() {
        return jdbcTemplate.query(
                con -> con.prepareStatement("SELECT id, name FROM mpa_ratings"),
                (rs, rowNum) -> extractMpaRating(rs)
        );
    }
}
