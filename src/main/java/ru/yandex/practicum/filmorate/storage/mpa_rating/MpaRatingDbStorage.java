package ru.yandex.practicum.filmorate.storage.mpa_rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.PreparedStatement;
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
        return jdbcTemplate.query(
                con -> {
                    final PreparedStatement ps = con.prepareStatement("SELECT id, name FROM mpa_ratings WHERE id = ?");
                    ps.setInt(1, id);
                    return ps;
                },
                this::extractGenre
        );
    }

    private MpaRating extractGenre(ResultSet rs) throws SQLException {
        return MpaRating.builder()
                .id(rs.getInt(1))
                .name(rs.getString(2))
                .build();
    }

    @Override
    public List<MpaRating> getAll() {
        return jdbcTemplate.query(
                con -> con.prepareStatement("SELECT id, name FROM mpa_ratings"),
                (rs, rowNum) -> extractGenre(rs)
        );
    }
}
